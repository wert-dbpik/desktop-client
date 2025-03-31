package ru.wert.tubus.chogori.chat.socketwork;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import ru.wert.tubus.chogori.application.app_window.ApplicationController;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.application.services.ChogoriServices;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Класс для создания и отображения push-уведомлений в стиле чата.
 */
public class PushNotification {

    private VBox vbMessageContainer; // Самый верхний контейнер для сообщения
    private VBox vbOutlineMessage;   // Контейнер, включающий заголовок, сообщение и время создания
    private VBox vbMessage;          // Контейнер для самого сообщения
    private Label lblFrom;           // Метка для отображения имени отправителя
    private Label lblDate;           // Метка для отображения даты сообщения
    private Label lblTitle;          // Метка для отображения заголовка сообщения
    private Label lblTime;           // Метка для отображения времени сообщения

    /**
     * Создает и отображает push-уведомление с заданным сообщением.
     *
     * @param message Сообщение для уведомления.
     */
    public static void show(Message message) {
        Platform.runLater(() -> {
            Stage notificationStage = new Stage();
            notificationStage.initStyle(StageStyle.UTILITY);
            notificationStage.initModality(Modality.NONE);
            notificationStage.setAlwaysOnTop(true);
            notificationStage.setResizable(false);

            // Инициализация элементов
            PushNotification notification = new PushNotification();
            notification.initElements();

            // Установка данных сообщения
            User sender = ChogoriServices.CH_USERS.findById(message.getSenderId());
            if (sender == null) return;

            // Показываем уведомление только если сообщение не от текущего пользователя
            if (sender.equals(CH_CURRENT_USER)) return;

            notification.lblFrom.setText(sender.getName());
            notification.lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime().toString()));
            notification.lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime().toString()));

            // Обработка текста сообщения
            Label text = new Label(message.getText());
            text.setWrapText(true);
            text.setMaxWidth(250);
            text.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
            notification.vbMessage.getChildren().add(text);

            // Стилизация контейнеров
            notification.vbMessageContainer.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-background-radius: 10; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #d0d0d0; " +
                            "-fx-border-width: 1; " +
                            "-fx-padding: 10;"
            );

            notification.vbOutlineMessage.setStyle("-fx-spacing: 5;");
            notification.lblFrom.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            notification.lblTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

            // Создание сцены
            VBox root = new VBox(notification.vbMessageContainer);
            root.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

            Scene scene = new Scene(root);
            scene.setFill(null);
            notificationStage.setScene(scene);

            // Позиционирование уведомления относительно кнопки чата
            if (ApplicationController.chat != null && ApplicationController.chat.getBtnChat() != null) {
                Button chatButton = ApplicationController.chat.getBtnChat();
                Bounds bounds = chatButton.localToScreen(chatButton.getBoundsInLocal());

                notificationStage.setX(bounds.getMinX() - 300); // Смещаем влево от кнопки
                notificationStage.setY(bounds.getMinY() - 150); // Поднимаем выше кнопки

                // Проверка границ экрана
                Screen screen = Screen.getPrimary();
                Rectangle2D screenBounds = screen.getVisualBounds();

                if (notificationStage.getX() < screenBounds.getMinX()) {
                    notificationStage.setX(screenBounds.getMinX() + 10);
                }

                if (notificationStage.getY() < screenBounds.getMinY()) {
                    notificationStage.setY(screenBounds.getMinY() + 10);
                }
            }

            notificationStage.show();

            // Автоматическое закрытие уведомления через 5 секунд
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(notificationStage::close);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    /**
     * Инициализирует элементы уведомления.
     */
    private void initElements() {
        vbMessageContainer = new VBox();
        vbOutlineMessage = new VBox();
        vbMessage = new VBox();

        lblFrom = new Label();
        lblDate = new Label();
        lblTitle = new Label();
        lblTime = new Label();

        // Настройка контейнеров
        vbMessageContainer.getChildren().addAll(lblFrom, vbOutlineMessage, lblTime);
        vbOutlineMessage.getChildren().add(vbMessage);

        vbMessageContainer.setAlignment(Pos.TOP_LEFT);
        vbMessageContainer.setSpacing(5);
        vbOutlineMessage.setSpacing(5);
        vbMessage.setSpacing(5);
    }
}
