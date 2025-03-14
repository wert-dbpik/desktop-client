package ru.wert.tubus.chogori.chat.socketwork;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

/**
 * Класс для создания и отображения push-уведомлений.
 */
public class PushNotification {

    /**
     * Создает и отображает push-уведомление с заданным текстом.
     *
     * @param messageText Текст уведомления.
     */
    public static void show(String messageText) {
        Platform.runLater(() -> {
            Stage notificationStage = new Stage();
            notificationStage.initStyle(StageStyle.UTILITY);
            notificationStage.initModality(Modality.NONE);
            notificationStage.setAlwaysOnTop(true);

            // Устанавливаем позицию уведомления в правом нижнем углу
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double x = screenBounds.getMaxX() - 300; // Ширина уведомления 300px
            double y = screenBounds.getMaxY() - 100; // Высота уведомления 100px
            notificationStage.setX(x);
            notificationStage.setY(y);

            Label label = new Label(messageText);
            label.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10px;");

            StackPane root = new StackPane(label);
            root.setStyle("-fx-background-color: #333; -fx-padding: 10px;");

            Scene scene = new Scene(root, 300, 100);
            notificationStage.setScene(scene);
            notificationStage.show();

            // Автоматическое закрытие уведомления через 5 секунд
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> notificationStage.close());
            }).start();
        });
    }
}
