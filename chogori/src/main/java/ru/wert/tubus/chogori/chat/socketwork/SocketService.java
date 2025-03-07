package ru.wert.tubus.chogori.chat.socketwork;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.retrofit.AppProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SocketService {

    // Адрес сервера и порт
    private static final String SERVER_ADDRESS = AppProperties.getInstance().getIpAddress();
    private static final int PORT = 8081;

    // Тайм-ауты
    private static final int RECONNECT_DELAY_MS = 5000;
    private static final int SOCKET_TIMEOUT_MS = 30000;
    private static final int CONNECT_TIMEOUT_MS = 5000;

    // Флаг для управления работой сервиса
    private static volatile boolean running = true;

    // Очередь для хранения сообщений
    private static final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    // Сокет и потоки ввода/вывода
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    // Gson для сериализации/десериализации
    private static final Gson gson = new Gson();

    // Сервис для управления фоновыми задачами
    private static final Service<Void> socketService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (running) {
                        try {
                            // Подключение к серверу
                            connectToServer();

                            // Отправка сообщения USER_IN
                            ServiceMessaging.sendMessageUserIn(AppProperties.getInstance().getLastUser());

                            // Запуск потоков для получения и отправки сообщений
                            startMessageHandlers();

                            log.info("Socket successfully connected and threads started.");

                            // Ожидание завершения работы
                            while (running && !socket.isClosed()) {
                                Thread.sleep(1000);
                            }

                        } catch (IOException e) {
                            log.error("Error connecting to the server: {}", e.getMessage());
                        } catch (Exception e) {
                            log.error("Unexpected error: {}", e.getMessage(), e);
                        } finally {
                            closeResources();
                            if (running) {
                                log.info("Attempting to reconnect in {} ms...", RECONNECT_DELAY_MS);
                                Thread.sleep(RECONNECT_DELAY_MS);
                            }
                        }
                    }
                    log.info("Socket service stopped.");
                    return null;
                }
            };
        }
    };

    // Метод для запуска сервиса
    public static void start() {
        if (!socketService.isRunning()) {
            socketService.restart();
        }
    }

    // Метод для остановки сервиса
    public static void stop() {
        running = false;
        socketService.cancel();
        closeResources();
        log.info("Socket service is shutting down...");
    }

    // Метод для подключения к серверу
    private static void connectToServer() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), CONNECT_TIMEOUT_MS);
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        log.info("Connection to server {}:{} established.", SERVER_ADDRESS, PORT);
    }

    // Метод для запуска обработчиков сообщений
    private static void startMessageHandlers() {
        // Задача для получения сообщений
        Task<Void> receiveMessagesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (running) {
                    try {
                        String serverMessage = in.readLine();
                        if (serverMessage != null) {
                            Message message = gson.fromJson(serverMessage, Message.class);
                            // Обновляем UI через Platform.runLater
                            Platform.runLater(() -> {
                                // Здесь можно обновить UI (например, добавить сообщение в чат)
                                log.debug("Message received from server: {}", message.toUsefulString());
                            });
                        }
                    } catch (SocketTimeoutException e) {
                        // Продолжаем ожидание новых сообщений
                        continue;
                    } catch (IOException e) {
                        if (running) {
                            log.error("Error receiving messages: {}", e.getMessage());
                            closeResources();
                            break;
                        }
                    }
                }
                log.info("Message receiving thread stopped.");
                return null;
            }
        };

        // Задача для отправки сообщений
        Task<Void> sendMessagesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (running) {
                    try {
                        Message message = messageQueue.take();
                        if (socket != null && socket.isConnected() && !socket.isClosed()) {
                            String jsonMessage = gson.toJson(message);
                            log.debug("Sending message to server: {}", jsonMessage);
                            out.println(jsonMessage);
                        } else {
                            log.warn("Socket is not connected, message not sent: {}", message.toUsefulString());
                            messageQueue.put(message);
                            Thread.sleep(RECONNECT_DELAY_MS);
                        }
                    } catch (InterruptedException e) {
                        log.warn("Message sending thread interrupted: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        if (running) {
                            log.error("Error sending messages: {}", e.getMessage());
                        }
                    }
                }
                log.info("Message sending thread stopped.");
                return null;
            }
        };

        // Запуск задач в отдельных потоках
        new Thread(receiveMessagesTask).start();
        new Thread(sendMessagesTask).start();
    }

    // Метод для добавления сообщения в очередь
    public static void sendMessage(Message message) {
        try {
            messageQueue.put(message);
            log.debug("Message added to the queue: {}", message.toUsefulString());
        } catch (InterruptedException e) {
            log.error("Error adding message to the queue: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // Метод для закрытия ресурсов
    private static void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            log.info("Socket resources closed.");
        } catch (IOException e) {
            log.error("Error closing resources: {}", e.getMessage());
        }
    }
}

