package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class MessageSender {

    // Очередь сообщений для отправки на сервер
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    // Поток для отправки данных на сервер
    private final PrintWriter out;
    // Флаг для управления циклом работы потока
    private volatile boolean running = true;
    // Задержка перед повторной попыткой отправки сообщения при отсутствии подключения
    private static final int RECONNECT_DELAY_MS = 5000;

    // Конструктор, принимающий PrintWriter для отправки данных на сервер
    public MessageSender(PrintWriter out) {
        this.out = out;
    }

    // Метод для запуска потока, который отправляет сообщения на сервер
    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    // Блокирующее получение сообщения из очереди
                    Message message = messageQueue.take();
                    if (out != null) {
                        // Преобразование объекта Message в JSON-строку
                        String jsonMessage = new Gson().toJson(message);
                        // Логирование отправляемого сообщения
                        log.debug("Отправка сообщения на сервер: {}", jsonMessage);
                        // Отправка JSON-строки на сервер
                        out.println(jsonMessage);
                    } else {
                        // Логирование предупреждения, если сокет не подключен
                        log.warn("Сокет не подключен, сообщение не отправлено: {}", message.toUsefulString());
                        // Возвращение сообщения в очередь для повторной попытки отправки
                        messageQueue.put(message);
                        // Задержка перед повторной попыткой
                        Thread.sleep(RECONNECT_DELAY_MS);
                    }
                } catch (InterruptedException e) {
                    // Логирование прерывания потока
                    log.warn("Поток отправки сообщений прерван: {}", e.getMessage());
                    // Повторное установление флага прерывания
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    if (running) {
                        // Логирование ошибки при отправке сообщения
                        log.error("Ошибка при отправке сообщений: {}", e.getMessage());
                    }
                }
            }
            // Логирование остановки потока отправки сообщений
            log.info("Поток отправки сообщений остановлен.");
        }).start();
    }

    // Метод для добавления сообщения в очередь на отправку
    public void sendMessage(Message message) {
        try {
            // Добавление сообщения в очередь
            messageQueue.put(message);
            // Логирование добавления сообщения в очередь
            log.debug("Сообщение добавлено в очередь: {}", message.toUsefulString());
        } catch (InterruptedException e) {
            // Логирование ошибки при добавлении сообщения в очередь
            log.error("Ошибка при добавлении сообщения в очередь: {}", e.getMessage());
            // Повторное установление флага прерывания
            Thread.currentThread().interrupt();
        }
    }

    // Метод для остановки потока отправки сообщений
    public void stop() {
        running = false;
    }
}
