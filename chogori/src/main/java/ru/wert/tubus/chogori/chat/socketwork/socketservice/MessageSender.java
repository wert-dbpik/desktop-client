package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.entity.models.Message;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static ru.wert.tubus.winform.statics.WinformStatic.USE_HEARTBEAT;

@Slf4j
public class MessageSender {

    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final PrintWriter out;
    private volatile boolean running = true;
    private static final int RECONNECT_DELAY_MS = 5000;
    private static final int HEARTBEAT_INTERVAL =
            USE_HEARTBEAT ?
                    15 * 1000 : // 15000 = 15  секунд
                    15 * 1000 * 1000;

    public MessageSender(PrintWriter out) {
        this.out = out;
        startHeartbeatSender();
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    Message message = messageQueue.take();
                    sendMessageToServer(message);
                } catch (InterruptedException e) {
                    log.warn("Поток отправки сообщений прерван: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    if (running) {
                        log.error("Ошибка при отправке сообщений: {}", e.getMessage());
                    }
                }
            }
            log.info("Поток отправки сообщений остановлен.");
        }).start();
    }

    private void startHeartbeatSender() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);
                    Message heartbeat = new Message();
                    heartbeat.setType(Message.MessageType.HEARTBEAT);
                    sendMessageToServer(heartbeat);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void sendMessageToServer(Message message) {
        if (out != null) {
            String jsonMessage = GsonConfiguration.createGson().toJson(message);
            out.println(jsonMessage);
            if(!message.getType().equals(Message.MessageType.HEARTBEAT))
                log.info("Сообщение отправлено на сервер: {}", jsonMessage);
        } else {
            log.warn("Сокет не подключен, сообщение не отправлено: {}", message.toUsefulString());
            try {
                messageQueue.put(message);
                Thread.sleep(RECONNECT_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
            log.debug("Сообщение добавлено в очередь: {}", message.toUsefulString());
        } catch (InterruptedException e) {
            log.error("Ошибка при добавлении сообщения в очередь: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        running = false;
    }
}
