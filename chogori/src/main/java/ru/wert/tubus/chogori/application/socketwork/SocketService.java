package ru.wert.tubus.chogori.application.socketwork;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.retrofit.AppProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SocketService {

    private static final String SERVER_ADDRESS = AppProperties.getInstance().getIpAddress();
    private static final int PORT = 8080;
    private static volatile boolean running = true;
    private static final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    /**
     * Запускает сервис в отдельном потоке.
     */
    public static void start() {
        new Thread(() -> {
            try (Socket socket = new Socket();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Устанавливаем таймаут для соединения (5 секунд)
                socket.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), 5000);

                log.info("Socket for message exchange has been created by server ip '{}' on port {}", SERVER_ADDRESS, PORT);

                ServerMessageHandler handler = new ServerMessageHandler();

                // Запускаем поток для чтения сообщений
                Thread receiveThread = receiveMessagesThread(in, handler);
                // Запускаем поток для отправки сообщений
                Thread sendThread = sendMessagesThread(out);

                // Не используем join(), чтобы не блокировать основной поток
                receiveThread.start();
                sendThread.start();

            } catch (IOException e) {
                log.error("An error occurred while managing the socket connection", e);
            } finally {
                running = false;
            }
        }).start(); // Запускаем сервис в отдельном потоке
    }

    /**
     * Создает поток для чтения сообщений от сервера.
     */
    private static Thread receiveMessagesThread(BufferedReader in, ServerMessageHandler handler) {
        return new Thread(() -> {
            try {
                String serverMessage;
                while (running && (serverMessage = in.readLine()) != null) {
                    handler.handle(serverMessage);
                }
            } catch (IOException e) {
                log.error("Error receiving messages from server", e);
            }
        });
    }

    /**
     * Создает поток для отправки сообщений на сервер.
     */
    private static Thread sendMessagesThread(PrintWriter out) {
        return new Thread(() -> {
            try {
                while (running) {
                    // Берем сообщение из очереди (блокируется, если очередь пуста)
                    String message = messageQueue.take();
                    out.println(message);
                }
            } catch (InterruptedException e) {
                log.error("Error while sending messages to server", e);
            }
        });
    }

    /**
     * Добавляет сообщение в очередь для отправки на сервер.
     */
    public static void sendMessage(String message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            log.error("Error while adding message to the queue", e);
        }
    }

    /**
     * Останавливает сервис.
     */
    public static void stop() {
        running = false;
    }

//    /**
//     * Точка входа в программу.
//     */
//    public static void main(String[] args) {
//        // Добавляем shutdown hook для корректного завершения работы
//        Runtime.getRuntime().addShutdownHook(new Thread(SocketService::stop));
//        // Запускаем сервис
//        SocketService.start();
//
//        // Пример отправки сообщений
//        SocketService.sendMessage("Hello, Server!");
//        SocketService.sendMessage("How are you?");
//    }
}
