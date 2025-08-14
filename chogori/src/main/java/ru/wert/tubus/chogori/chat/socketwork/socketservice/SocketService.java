package ru.wert.tubus.chogori.chat.socketwork.socketservice;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.client.entity.models.Message;

import java.io.IOException;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class SocketService {

    public static BooleanProperty CHAT_SERVER_AVAILABLE_PROPERTY = new SimpleBooleanProperty(true);

    // Задержка перед повторной попыткой подключения к серверу (в миллисекундах)
    private static final int RECONNECT_DELAY_MS = 5000;
    // Флаг для управления циклом работы сервиса
    private static volatile boolean running = true;

    // Менеджер для управления соединением с сервером
    private static final SocketConnectionManager connectionManager = new SocketConnectionManager();
    // Получатель сообщений от сервера
    private static MessageReceiver messageReceiver;
    // Отправитель сообщений на сервер
    private static MessageSender messageSender;

    // Сервис для работы с сокетом в фоновом потоке
    private static final Service<Void> socketService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (running) {
                        try {
                            // Подключение к серверу
                            connectionManager.connect();

                            // Инициализация и запуск потоков для получения и отправки сообщений
                            messageReceiver = new MessageReceiver(connectionManager.getIn());
                            messageSender = new MessageSender(connectionManager.getOut());

                            messageReceiver.start();
                            messageSender.start();

                            // Отправка сообщения USER_IN для уведомления сервера о входе пользователя
                            ServiceMessaging.sendMessageUserIn(CH_CURRENT_USER.getId());

                            // Логирование успешного подключения и запуска потоков
                            log.info("Сокет успешно подключен, потоки запущены.");

                            CHAT_SERVER_AVAILABLE_PROPERTY.set(true);

                            // Ожидание завершения работы сервиса
                            while (running && connectionManager.isConnected()) {
                                Thread.sleep(1000);
                            }

                        } catch (IOException e) {
                            // Логирование ошибки подключения к серверу
                            log.error("Ошибка подключения к серверу: {}", e.getMessage());
                            //TODO: Добавить оповещение и отключение оповещения
                            CHAT_SERVER_AVAILABLE_PROPERTY.setValue(false);
                        } catch (Exception e) {
                            // Логирование непредвиденной ошибки
                            log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
                        } finally {
                            // Закрытие соединения с сервером
                            connectionManager.close();
                            if (running) {
                                // Логирование попытки переподключения
                                log.info("Попытка переподключения через {} мс...", RECONNECT_DELAY_MS);
                                Thread.sleep(RECONNECT_DELAY_MS);
                            }
                        }
                    }
                    // Логирование остановки сервиса
                    log.info("Сервис сокета остановлен.");
                    return null;
                }
            };
        }
    };

    public static void reconnect() {
        Platform.runLater(() -> {
            try {
                log.info("Инициировано переподключение...");
                connectionManager.close();
                if (messageReceiver != null) messageReceiver.stop();
                if (messageSender != null) messageSender.stop();
                socketService.restart();
            } catch (Exception e) {
                log.error("Ошибка при переподключении: {}", e.getMessage());
            }
        });
    }

    // Метод для запуска сервиса сокета
    public static void start() {
        if (!socketService.isRunning()) {
            socketService.restart();
        }
    }

    // Метод для остановки сервиса сокета
    public static void stop() {
        ServiceMessaging.sendMessageUserOut();

        Platform.runLater(() -> {
            running = false;
            socketService.cancel();
            connectionManager.close();
            if (messageReceiver != null) messageReceiver.stop();
            if (messageSender != null) messageSender.stop();
            log.info("Сервис сокета завершает работу...");
        });
    }

    // Метод для отправки сообщения на сервер
    public static void sendMessage(Message message) {
        if (messageSender != null) {
            messageSender.sendMessage(message);
        } else
            log.error("не удалось отправить сообщение {}, т.к. messageSender = null", message.toUsefulString());
    }
}

