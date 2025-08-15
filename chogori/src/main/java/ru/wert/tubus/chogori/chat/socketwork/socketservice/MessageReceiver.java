package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import com.google.gson.Gson;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.utils.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Класс для приема сообщений от сервера.
 * Обрабатывает входящие сообщения и heartbeat-пакеты.
 */
@Slf4j
public class MessageReceiver {

    private final BufferedReader in;
    private final Gson gson = GsonConfiguration.createGson();
    private volatile boolean running = true;
    private long lastHeartbeatTime = System.currentTimeMillis();
    private static final long HEARTBEAT_TIMEOUT = 30000; // 30 секунд

    /**
     * Конструктор.
     * @param in входной поток для чтения сообщений от сервера
     */
    public MessageReceiver(BufferedReader in) {
        this.in = in;
    }

    /**
     * Запускает поток для приема сообщений.
     */
    public void start() {
        new Thread(this::receiveMessages).start();
    }

    private void receiveMessages() {
        while (running) {
            try {
                processIncomingMessage();
                checkHeartbeatTimeout();
            } catch (SocketTimeoutException e) {
                handleSocketTimeout();
            } catch (IOException e) {
                handleIOException(e);
            }
        }
        log.info("Поток получения сообщений остановлен.");
    }

    private void processIncomingMessage() throws IOException {
        String serverMessage = in.readLine();
        if (serverMessage != null) {
            Message message = gson.fromJson(serverMessage, Message.class);
            if (message.getType() == MessageType.HEARTBEAT) {
                handleHeartbeat();
            } else {
                handleRegularMessage(message);
            }
        }
    }

    private void handleHeartbeat() {
        lastHeartbeatTime = System.currentTimeMillis();
//        log.debug("Получен HEARTBEAT от сервера");
        SocketService.updateServerStatus(true);
    }

    private void handleRegularMessage(Message message) {
        Platform.runLater(() -> {
            ServerMessageHandler.handle(message);
            log.debug("Сообщение получено: {}", message.toUsefulString());
        });
    }

    private void checkHeartbeatTimeout() {
        if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
            log.warn("Превышен таймаут HEARTBEAT (30 сек)");
            initiateReconnection();
        }
    }

    private void handleSocketTimeout() {
        if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_TIMEOUT) {
            log.warn("Таймаут соединения");
            initiateReconnection();
        }
    }

    private void handleIOException(IOException e) {
        if (running) {
            log.error("Ошибка соединения: {}", e.getMessage());
            initiateReconnection();
        }
    }

    private void initiateReconnection() {
        SocketService.updateServerStatus(false);
        SocketService.reconnect();
        running = false;
    }

    /** Останавливает прием сообщений */
    public void stop() {
        running = false;
    }
}