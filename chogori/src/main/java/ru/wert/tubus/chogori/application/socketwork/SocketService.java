package ru.wert.tubus.chogori.application.socketwork;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.retrofit.AppProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

@Slf4j
public class SocketService {

    // Адрес сервера, к которому подключаемся
    private static final String SERVER_ADDRESS = AppProperties.getInstance().getIpAddress();
    // Порт сервера
    private static final int PORT = 8081;
    // Задержка перед повторным подключением (в миллисекундах)
    private static final int RECONNECT_DELAY_MS = 5000;
    // Тайм-аут для операций с сокетом (в миллисекундах)
    private static final int SOCKET_TIMEOUT_MS = 30000;
    // Тайм-аут для подключения к серверу (в миллисекундах)
    private static final int CONNECT_TIMEOUT_MS = 5000;

    // Флаг для управления работой сервиса (работает/остановлен)
    private static volatile boolean running = true;
    // Очередь для хранения сообщений, которые нужно отправить на сервер
    private static final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    // Пул потоков для обработки подключений и отправки/получения сообщений
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    // Сокет для подключения к серверу
    private static Socket socket;
    // Поток для отправки данных на сервер
    private static PrintWriter out;
    // Поток для чтения данных от сервера
    private static BufferedReader in;

    // Gson для сериализации и десериализации JSON
    private static final Gson gson = new Gson();

    // Метод для запуска сервиса
    public static void start() {
        executorService.submit(() -> {
            while (running) {
                try {
                    // Подключение к серверу
                    connectToServer();
                    // Обработчик сообщений от сервера
                    ServerMessageHandler handler = new ServerMessageHandler();

                    // Запуск потока для получения сообщений от сервера
                    executorService.submit(receiveMessagesThread(handler));
                    // Запуск потока для отправки сообщений на сервер
                    executorService.submit(sendMessagesThread());

                    log.info("Socket successfully connected and threads started.");

                    // Ожидание завершения работы потоков
                    while (running && !socket.isClosed()) {
                        sleep(1000); // Проверяем состояние сокета каждую секунду
                    }

                } catch (IOException e) {
                    log.error("Error connecting to the server: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Unexpected error: {}", e.getMessage(), e);
                } finally {
                    // Закрытие ресурсов
                    closeResources();
                    if (running) {
                        log.info("Attempting to reconnect in {} ms...", RECONNECT_DELAY_MS);
                        sleep(RECONNECT_DELAY_MS); // Пауза перед повторным подключением
                    }
                }
            }
            log.info("Socket service stopped.");
        });
    }

    // Метод для подключения к серверу
    private static void connectToServer() throws IOException {
        socket = new Socket();
        // Подключение к серверу с указанием тайм-аута
        socket.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), CONNECT_TIMEOUT_MS);
        // Установка тайм-аута для операций с сокетом
        socket.setSoTimeout(SOCKET_TIMEOUT_MS);

        // Инициализация потока для отправки данных
        out = new PrintWriter(socket.getOutputStream(), true);
        // Инициализация потока для чтения данных
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        log.info("Connection to server {}:{} established.", SERVER_ADDRESS, PORT);
    }

    // Метод для создания потока получения сообщений от сервера
    private static Runnable receiveMessagesThread(ServerMessageHandler handler) {
        return () -> {
            try {
                String serverMessage;
                // Чтение сообщений от сервера
                while (running && (serverMessage = in.readLine()) != null) {
                    log.debug("Message received from server: {}", serverMessage);
                    // Десериализация JSON в объект Message
                    Message message = gson.fromJson(serverMessage, Message.class);
                    // Обработка сообщения
                    handler.handle(message);
                }
            } catch (SocketTimeoutException e) {
                if (running) {
                    log.warn("Read timeout. Waiting for new messages...");
                }
            } catch (IOException e) {
                if (running) {
                    log.error("Error receiving messages: {}", e.getMessage());
                }
            } finally {
                log.info("Message receiving thread stopped.");
            }
        };
    }

    // Метод для создания потока отправки сообщений на сервер
    private static Runnable sendMessagesThread() {
        return () -> {
            try {
                while (running && !Thread.currentThread().isInterrupted()) {
                    // Получение сообщения из очереди
                    Message message = messageQueue.take();
                    if (socket != null && socket.isConnected() && !socket.isClosed()) {
                        // Сериализация объекта Message в JSON
                        String jsonMessage = gson.toJson(message);
                        log.debug("Sending message to server: {}", jsonMessage);
                        // Отправка сообщения на сервер
                        out.println(jsonMessage);
                    } else {
                        log.warn("Socket is not connected, message not sent: {}", message);
                        // Если сокет не подключен, добавляем сообщение обратно в очередь
                        messageQueue.put(message);
                        sleep(RECONNECT_DELAY_MS); // Пауза перед повторной попыткой
                    }
                }
            } catch (InterruptedException e) {
                log.warn("Message sending thread interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt(); // Восстановление статуса прерывания
            } catch (Exception e) {
                if (running) {
                    log.error("Error sending messages: {}", e.getMessage());
                }
            } finally {
                log.info("Message sending thread stopped.");
            }
        };
    }

    // Метод для добавления сообщения в очередь на отправку
    public static void sendMessage(Message message) {
        try {
            messageQueue.put(message);
            log.debug("Message added to the queue: {}", message);
        } catch (InterruptedException e) {
            log.error("Error adding message to the queue: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // Метод для остановки сервиса
    public static void stop() {
        running = false;
        executorService.shutdownNow(); // Прерывание всех потоков
        closeResources();
        log.info("Socket service is shutting down...");
    }

    // Метод для закрытия ресурсов (сокет, потоки ввода/вывода)
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

    // Метод для приостановки потока на указанное время
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn("Sleep interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt(); // Восстановление статуса прерывания
        }
    }
}

