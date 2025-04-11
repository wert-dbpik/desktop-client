package ru.wert.tubus.chogori.chat.socketwork;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.app_window.ApplicationController;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.MessageCardsRenderer;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class PushNotification {
    // Константы стилей
    private static final String HEADER_STYLE = "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0 0 5 0; -fx-text-fill: #333;";
    private static final String NOTIFICATION_STYLE = "-fx-background-color: #f8f8f8; -fx-padding: 10; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;";
    private static final String MESSAGE_CONTENT_STYLE = "-fx-background-color: #ffffff; -fx-padding: 10; " +
            "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 3; -fx-background-radius: 3;";

    // Размеры и отступы
    private static final double NOTIFICATION_WIDTH = 300;
    private static final double NOTIFICATION_HEIGHT = 120;
    private static final double SPACING_BETWEEN_NOTIFICATIONS = 5;
    private static final double PADDING_FROM_SCREEN_EDGE = 15;
    private static final double FADE_DURATION_MS = 350;
    private static final int AUTO_CLOSE_DELAY_SECONDS = 20;

    // Анимация
    private static final double FADE_OUT_DURATION = 500;

    // Коллекции для управления уведомлениями
    private static final Map<Long, Stage> activeNotifications = new ConcurrentHashMap<>();
    private static final List<Stage> notificationStack = new CopyOnWriteArrayList<>();
    private static final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService workerPool = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private static SideChat sideChat;

    static {
        cleanupExecutor.scheduleAtFixedRate(
                () -> activeNotifications.clear(),
                10, 10, TimeUnit.MINUTES
        );
    }

    public static void init(SideChat chat) {
        sideChat = chat;
    }

    public static void show(Message message) {
        if (message == null || message.getRoomId() == null) return;

        workerPool.submit(() -> {
            try {
                if (shouldSkipNotification(message)) return;

                User sender = loadSender(message.getSenderId());

                Platform.runLater(() -> {
                    closeExistingNotification(message.getRoomId());

                    VBox notificationContainer = createNotificationContainer(sender, message);
                    Stage stage = createStage(notificationContainer);

                    // Устанавливаем иконку приложения для уведомления
                    if (WinformStatic.WF_MAIN_STAGE != null) {
                        stage.getIcons().addAll(WinformStatic.WF_MAIN_STAGE.getIcons());
                    }

                    // Позиционируем уведомление до показа
                    positionNewNotification(stage);

                    setupNotificationBehavior(stage, message.getRoomId(), notificationContainer, message);

                    stage.setOnShown(e -> {
                        activeNotifications.put(message.getRoomId(), stage);
                        notificationStack.add(stage);

                        // Если приложение свернуто - просто показываем уведомление
                        if (WinformStatic.WF_MAIN_STAGE != null && WinformStatic.WF_MAIN_STAGE.isIconified()) {
                            stage.setAlwaysOnTop(true); // Гарантируем видимость
                        }
                    });

                    stage.show();
//                    scheduleAutoClose(stage, message.getRoomId());
                });
            } catch (Exception e) {
                log.error("Ошибка при показе уведомления", e);
            }
        });
    }

    private static void setupNotificationBehavior(Stage stage, Long roomId, Node container, Message message) {
        FadeTransition hoverFade = new FadeTransition(Duration.millis(FADE_DURATION_MS), container);
        hoverFade.setFromValue(1.0);
        hoverFade.setToValue(0.7);
        hoverFade.setCycleCount(2);
        hoverFade.setAutoReverse(true);

        container.setOnMouseEntered(e -> hoverFade.playFromStart());
        container.setOnMouseClicked(e -> {
            closeNotificationWithFade(stage, roomId);
            openChatForMessage(message);
        });
    }

    private static void openChatForMessage(Message message) {
        if (sideChat == null) return;

        // Закрываем все уведомления перед открытием чата
        closeAllNotifications();

        // Получаем комнату из сообщения
        Room room = ChogoriServices.CH_ROOMS.findById(message.getRoomId());
        if (room == null) return;

        Platform.runLater(() -> {
            try {
                // Разворачиваем главное окно, если оно свернуто
                if (WinformStatic.WF_MAIN_STAGE != null && WinformStatic.WF_MAIN_STAGE.isIconified()) {
                    WinformStatic.WF_MAIN_STAGE.setIconified(false);
                }

                // Если чат закрыт - открываем его
                if (ApplicationController.chat != null && !ApplicationController.chat.isChatOpen()) {
                    ApplicationController.chat.openChat();
                }

                // Открываем чат с нужной комнатой
                sideChat.showChatDialog(room);

                // Помечаем сообщения как доставленные
                sideChat.getDialogController().markMessagesAsDelivered(room);
            } catch (Exception e) {
                log.error("Ошибка при открытии чата из уведомления", e);
            }
        });
    }

    public static void closeAllNotifications() {
        Platform.runLater(() -> {
            // Создаем копию списка, чтобы избежать ConcurrentModificationException
            List<Stage> stagesToClose = new ArrayList<>(notificationStack);

            for (Stage stage : stagesToClose) {
                if (stage != null && stage.isShowing()) {
                    // Находим roomId для этого stage
                    Long roomIdToRemove = activeNotifications.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(stage))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);

                    // Закрываем с анимацией
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_OUT_DURATION), stage.getScene().getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e -> {
                        stage.close();
                        if (roomIdToRemove != null) {
                            activeNotifications.remove(roomIdToRemove);
                        }
                        notificationStack.remove(stage);
                    });
                    fadeOut.play();
                }
            }

            // Очищаем коллекции после закрытия всех уведомлений
            activeNotifications.clear();
            notificationStack.clear();
        });
    }

    private static void positionNewNotification(Stage stage) {
        Rectangle2D visualBounds = getCurrentScreenBounds();
        double startX = visualBounds.getMaxX() - NOTIFICATION_WIDTH - PADDING_FROM_SCREEN_EDGE;
        double startY = visualBounds.getMaxY() - PADDING_FROM_SCREEN_EDGE - NOTIFICATION_HEIGHT; // Начинаем с нижнего края

        // Вычисляем позицию для нового уведомления, учитывая уже показанные
        for (Stage existingStage : notificationStack) {
            if (existingStage.isShowing()) {
                startY -= (existingStage.getHeight() + SPACING_BETWEEN_NOTIFICATIONS);
            }
        }

        // Проверяем, чтобы уведомление не выходило за верхнюю границу экрана
        if (startY < visualBounds.getMinY()) {
            startY = visualBounds.getMinY() + PADDING_FROM_SCREEN_EDGE;
        }

        stage.setX(startX);
        stage.setY(startY);
    }

    private static VBox createNotificationContainer(User sender, Message message) {
        VBox notificationContainer = new VBox(5);
        notificationContainer.setStyle(NOTIFICATION_STYLE);
        notificationContainer.setPrefSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
        notificationContainer.setMaxSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);

        Label senderLabel = new Label(sender.getName());
        senderLabel.setStyle(HEADER_STYLE);

        VBox messageContent = new VBox();
        messageContent.setStyle(MESSAGE_CONTENT_STYLE);
        messageContent.setPrefHeight(NOTIFICATION_HEIGHT - 40);

        Label titleLabel = new Label();
        MessageCardsRenderer renderer = new MessageCardsRenderer(titleLabel);
        renderMessage(renderer, messageContent, message);

        notificationContainer.getChildren().addAll(senderLabel, messageContent);
        return notificationContainer;
    }

    private static Stage createStage(Node content) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.NONE);
        stage.initOwner(WinformStatic.WF_MAIN_STAGE);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);

        Scene scene = new Scene((Parent) content);
        stage.setScene(scene);

        return stage;
    }

    private static void closeNotificationWithFade(Stage stage, Long roomId) {
        if (stage == null || !stage.isShowing()) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_OUT_DURATION), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            try {
                stage.close();
                if (roomId != null) {
                    activeNotifications.remove(roomId);
                }
                notificationStack.remove(stage);
                repositionAllNotifications();
            } catch (Exception ex) {
                log.error("Ошибка при закрытии уведомления", ex);
            }
        });
        fadeOut.play();
    }

    private static void repositionAllNotifications() {
        Rectangle2D visualBounds = getCurrentScreenBounds();
        double startX = visualBounds.getMaxX() - NOTIFICATION_WIDTH - PADDING_FROM_SCREEN_EDGE;
        double currentY = visualBounds.getMaxY() - PADDING_FROM_SCREEN_EDGE - NOTIFICATION_HEIGHT; // Начинаем с нижнего края

        // Фильтруем только видимые и полностью инициализированные уведомления
        List<Stage> visibleNotifications = notificationStack.stream()
                .filter(stage -> stage.isShowing() && stage.getScene() != null)
                .collect(Collectors.toList());

        for (Stage stage : visibleNotifications) {
            // Проверяем, чтобы уведомление не выходило за верхнюю границу экрана
            if (currentY < visualBounds.getMinY()) {
                currentY = visualBounds.getMinY() + PADDING_FROM_SCREEN_EDGE;
            }

            stage.setX(startX);
            stage.setY(currentY);
            currentY -= (stage.getHeight() + SPACING_BETWEEN_NOTIFICATIONS);
        }
    }

    private static Rectangle2D getCurrentScreenBounds() {
        Stage mainStage = WinformStatic.WF_MAIN_STAGE;
        Screen targetScreen;

        if (mainStage == null || !mainStage.isShowing()) {
            targetScreen = Screen.getPrimary();
        } else {
            targetScreen = Screen.getScreens().stream()
                    .filter(screen -> screen.getBounds().contains(mainStage.getX(), mainStage.getY()))
                    .findFirst()
                    .orElse(Screen.getPrimary());
        }

        return targetScreen.getVisualBounds();
    }

    private static void renderMessage(MessageCardsRenderer renderer, VBox container, Message message) {
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
                fallback.setStyle("-fx-text-fill: #333;");
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

    private static void closeExistingNotification(Long roomId) {
        if (activeNotifications.containsKey(roomId)) {
            Stage oldStage = activeNotifications.get(roomId);
            closeNotificationWithFade(oldStage, roomId);
        }
    }

    /**
     * Задержка перед автозакрытием уведомления
     * НЕ ИСПОЛЬЗУЕТСЯ
     * @param stage
     * @param roomId
     */
    private static void scheduleAutoClose(Stage stage, Long roomId) {
        new Thread(() -> {
            try {
                Thread.sleep(AUTO_CLOSE_DELAY_SECONDS * 1000L);
                Platform.runLater(() -> {
                    if (stage.isShowing()) {
                        closeNotificationWithFade(stage, roomId);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static void shutdown() {
        workerPool.shutdown();
        cleanupExecutor.shutdown();
        Platform.runLater(() -> {
            notificationStack.forEach(stage -> {
                if (stage.isShowing() && stage.getScene() != null) {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_OUT_DURATION), stage.getScene().getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e -> stage.close());
                    fadeOut.play();
                }
            });
            notificationStack.clear();
            activeNotifications.clear();
        });
    }
}