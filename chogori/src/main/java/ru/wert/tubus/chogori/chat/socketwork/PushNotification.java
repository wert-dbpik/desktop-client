package ru.wert.tubus.chogori.chat.socketwork;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.stage.Screen;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.MessageRenderer;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.modal.ModalWindow;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class PushNotification {
    // Стилизация заголовка
    private static final String HEADER_STYLE =
            "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0 0 5 0;";

    // Очередь для управления уведомлениями (roomId -> Stage)
    private static final Map<Long, Stage> activeNotifications = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

    // Пул потоков для фоновых задач
    private static final ExecutorService workerPool = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    static {
        // Очистка устаревших ссылок каждые 10 минут
        cleanupExecutor.scheduleAtFixedRate(
                activeNotifications::clear,
                10, 10, TimeUnit.MINUTES
        );
    }

    public static void show(Message message) {
        if (message == null || message.getRoomId() == null) return;

        workerPool.submit(() -> {
            try {
                // Быстрая проверка перед обработкой
                if (shouldSkipNotification(message)) return;

                // Подготовка данных в фоне
                User sender = loadSender(message.getSenderId());

                Platform.runLater(() -> {
                    // Закрываем предыдущее уведомление для этой комнаты
                    closeExistingNotification(message.getRoomId());

                    // Создаем контейнер уведомления
                    VBox notificationContainer = new VBox(5);
                    notificationContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10;");

                    // Заголовок с именем отправителя
                    Label senderLabel = new Label(sender.getName());
                    senderLabel.setStyle(HEADER_STYLE);

                    // Контейнер для содержимого сообщения
                    VBox messageContent = new VBox();
                    messageContent.setStyle("-fx-background-color: white; -fx-padding: 10;");

                    // Используем MessageRenderer для отображения сообщения
                    Label titleLabel = new Label(); // Заголовок не нужен в уведомлении
                    MessageRenderer renderer = new MessageRenderer(titleLabel);
                    renderMessage(renderer, messageContent, message);

                    notificationContainer.getChildren().addAll(senderLabel, messageContent);

                    // Создаем и показываем Stage
                    Stage stage = createStage(notificationContainer);
                    positionAtScreenCorner(stage);
                    stage.show();

                    // Сохраняем ссылку
                    activeNotifications.put(message.getRoomId(), stage);

                    // Автозакрытие через 5 секунд
                    scheduleAutoClose(stage, message.getRoomId());
                });
            } catch (Exception e) {
                log.error("Ошибка при показе уведомления", e);
            }
        });
    }

    private static void renderMessage(MessageRenderer renderer, VBox container, Message message) {
        switch (message.getType()) {
            case CHAT_TEXT:
                renderer.mountText(container, message);
                break;
            case CHAT_PICS:
                renderer.mountPics(container, message);
                break;
            case CHAT_DRAFTS:
                renderer.mountDrafts(container, message);
                break;
            case CHAT_FOLDERS:
                renderer.mountFolders(container, message);
                break;
            case CHAT_PASSPORTS:
                renderer.mountPassports(container, message);
                break;
            default:
                Label fallback = new Label("Новый тип сообщения: " + message.getType());
                container.getChildren().add(fallback);
        }
    }

    private static boolean shouldSkipNotification(Message message) {
        return message.getSenderId().equals(CH_CURRENT_USER.getId()) ||
                !message.getType().name().startsWith("CHAT_");
    }

    private static User loadSender(Long senderId) {
        return ChogoriServices.CH_USERS.findById(senderId);
    }

    private static Stage createStage(Node content) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setScene(new Scene((Parent) content));
        return stage;
    }

    /**
     * Позиционирует уведомление в правом нижнем углу текущего монитора
     */
    private static void positionAtScreenCorner(Stage notificationStage) {
        // Принудительно применяем изменения размера
        notificationStage.sizeToScene();

        // Даём время на применение изменений размера
        Platform.runLater(() -> {
            Stage mainStage = WinformStatic.WF_MAIN_STAGE;
            Screen targetScreen;

            if (mainStage == null || !mainStage.isShowing()) {
                targetScreen = Screen.getPrimary();
            } else {
                targetScreen = Screen.getScreens().stream()
                        .filter(screen -> {
                            Rectangle2D bounds = screen.getBounds();
                            return bounds.contains(mainStage.getX(), mainStage.getY());
                        })
                        .findFirst()
                        .orElse(Screen.getPrimary());
            }

            Rectangle2D visualBounds = targetScreen.getVisualBounds();
            double padding = 20;
            double x = visualBounds.getMaxX() - notificationStage.getWidth() - padding;
            double y = visualBounds.getMaxY() - notificationStage.getHeight() - padding;

            notificationStage.setX(x);
            notificationStage.setY(y);

            log.debug("Positioning notification at screen {}: {:.1f}, {:.1f}",
                    targetScreen, x, y);
        });
    }

    /**
     * Фолбэк-позиционирование на основном мониторе
     */
    private static void positionAtPrimaryScreenCorner(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double padding = 20;
        double x = visualBounds.getMaxX() - stage.getWidth() - padding;
        double y = visualBounds.getMaxY() - stage.getHeight() - padding;

        Platform.runLater(() -> {
            stage.setX(x);
            stage.setY(y);
            log.debug("Fallback positioning at primary screen: {:.1f}, {:.1f}", x, y);
        });
    }

    private static void closeExistingNotification(Long roomId) {
        if (activeNotifications.containsKey(roomId)) {
            Stage oldStage = activeNotifications.get(roomId);
            if (oldStage != null) {
                Platform.runLater(oldStage::close);
            }
            activeNotifications.remove(roomId);
        }
    }

    private static void scheduleAutoClose(Stage stage, Long roomId) {
        Platform.runLater(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        if (stage.isShowing()) {
                            stage.close();
                            activeNotifications.remove(roomId);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    public static void shutdown() {
        workerPool.shutdown();
        cleanupExecutor.shutdown();
        Platform.runLater(() -> activeNotifications.values().forEach(Stage::close));
    }
}
