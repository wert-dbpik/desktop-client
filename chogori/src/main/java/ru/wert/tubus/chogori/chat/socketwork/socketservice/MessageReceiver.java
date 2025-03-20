package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import com.google.gson.Gson;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.entity.models.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageReceiver {

    private final BufferedReader in; // Поток для чтения данных от сервера
    private final Gson gson = GsonConfiguration.createGson(); // Объект для преобразования JSON в объекты Java и обратно
    private volatile boolean running = true; // Флаг для управления циклом работы потока

    // Конструктор, принимающий BufferedReader для чтения данных от сервера
    public MessageReceiver(BufferedReader in) {
        this.in = in;
    }

    // Метод для запуска потока, который будет получать сообщения от сервера
    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    // Чтение строки сообщения от сервера
                    String serverMessage = in.readLine();
                    if (serverMessage != null) {
                        // Преобразование JSON-строки в объект Message
                        Message message = gson.fromJson(serverMessage, Message.class);
                        // Запуск обработки сообщения в потоке JavaFX
                        Platform.runLater(() -> {
                            ServerMessageHandler.handle(message);
                            // Логирование полученного сообщения
                            log.debug("Сообщение получено от сервера: {}", message.toUsefulString());
                        });
                    }
                } catch (SocketTimeoutException e) {
                    // В случае тайм-аута продолжаем ожидание новых сообщений
                    continue;
                } catch (IOException e) {
                    if (running) {
                        // Логирование ошибки при получении сообщений
                        log.error("Ошибка при получении сообщений: {}", e.getMessage());
                        break;
                    }
                }
            }
            // Логирование остановки потока получения сообщений
            log.info("Поток получения сообщений остановлен.");
        }).start();
    }

    // Метод для остановки потока получения сообщений
    public void stop() {
        running = false;
    }
}
