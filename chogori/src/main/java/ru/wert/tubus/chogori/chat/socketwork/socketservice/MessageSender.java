package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.utils.MessageType;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.tubus.winform.statics.WinformStatic.USE_HEARTBEAT;

/**
 * Класс для отправки сообщений на сервер.
 * Обеспечивает очередь сообщений и heartbeat-пакеты.
 */
@Slf4j
public class MessageSender {

    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final PrintWriter out;
    private volatile boolean running = true;
    private static final int HEARTBEAT_INTERVAL = USE_HEARTBEAT ? 15000 : Integer.MAX_VALUE;

    /**
     * Конструктор.
     * @param out выходной поток для отправки сообщений
     */
    public MessageSender(PrintWriter out) {
        this.out = out;
        startHeartbeatSender();
    }

    /** Запускает отправку сообщений */
    public void start() {
        new Thread(this::sendMessages).start();
    }

    private void sendMessages() {
        while (running) {
            try {
                Message message = messageQueue.take();
                sendMessageToServer(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Поток отправки прерван");
            } catch (Exception e) {
                log.error("Ошибка отправки: {}", e.getMessage());
            }
        }
        log.info("Поток отправки остановлен");
    }

    private void startHeartbeatSender() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void sendHeartbeat() {
        Message heartbeat = new Message();
        heartbeat.setSenderId(CH_CURRENT_USER.getId());
        heartbeat.setType(MessageType.HEARTBEAT);
        sendMessageToServer(heartbeat);
    }

    private void sendMessageToServer(Message message) {
        try {
            if (out != null && !out.checkError()) {
                String json = GsonConfiguration.createGson().toJson(message);
                out.println(json);
                out.flush();
                if (!message.getType().equals(MessageType.HEARTBEAT)) {
                    log.info("Отправлено: {}", json);
                }
            } else {
                handleSendError(message);
            }
        } catch (Exception e) {
            handleSendError(message);
        }
    }

    private void handleSendError(Message message) {
        log.warn("Ошибка отправки: {}", message.toUsefulString());
        if (running) {
            requeueMessage(message);
            SocketService.reconnect();
        }
    }

    private void requeueMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Добавляет сообщение в очередь на отправку.
     * @param message сообщение для отправки
     */
    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
            log.debug("Добавлено в очередь: {}", message.toUsefulString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Останавливает отправку сообщений */
    public void stop() {
        running = false;
    }
}