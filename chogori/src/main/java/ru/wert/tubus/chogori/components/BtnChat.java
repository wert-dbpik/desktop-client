package ru.wert.tubus.chogori.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ru.wert.tubus.chogori.chat.SideChat;

import java.io.IOException;

import static ru.wert.tubus.chogori.images.BtnImages.CHAT_WHITE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_YELLOW_IMG;
import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_CHAT;

public class BtnChat{

    private final ImageView imageWhite = new ImageView(CHAT_WHITE_IMG); // Изображение кнопки в обычном состоянии (белый чат)
    private final ImageView imageYellow = new ImageView(CHAT_YELLOW_IMG); // Изображение кнопки в активном состоянии (желтый чат)
    private final BooleanProperty hasNewMessages = new SimpleBooleanProperty(false); // Состояние наличия новых сообщений
    private final Timeline timeline; // Анимация мигания
    private boolean open;
    private SideChat sideChat = new SideChat();

    /**
     * Конструктор кнопки чата.
     *
     */
    public BtnChat(Button button) {

        // Устанавливаем начальное изображение и подсказку
        button.setGraphic(imageWhite);
        button.setTooltip(new Tooltip("Открыть чат"));

        button.setStyle("-fx-background-color: rgb(50, 50, 50); -fx-fit-to-width: 16pt; -fx-fit-to-height: 16pt");

        // Настройка анимации мигания
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    if (hasNewMessages.get()) {
                        // Мигание: меняем изображение
                        if (button.getGraphic().equals(imageWhite)) {
                            button.setGraphic(imageYellow);
                        } else {
                            button.setGraphic(imageWhite);
                        }
                    }
                }
                ));

        timeline.setCycleCount(Timeline.INDEFINITE); // Бесконечное повторение анимации

        // Обработка нажатия на кнопку
        button.setOnMousePressed(e -> {
            openChat(); // Открываем чат
        });

        // Следим за изменением состояния наличия новых сообщений
        hasNewMessages.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Если есть новые сообщения, запускаем мигание
                timeline.play();
            } else {
                // Если новых сообщений нет, останавливаем мигание и возвращаем обычное изображение
                timeline.stop();
                button.setGraphic(imageWhite);
            }
        });
    }


    /**
     * ОТКРЫТЬ ЧАТ
     */
    private void openChat() {
        hasNewMessages.set(false); // Сбрасываем состояние новых сообщений
        open = !open;
        if(open) {
            SP_CHAT.setPrefWidth(CHAT_WIDTH);
            SP_CHAT.setMinWidth(CHAT_WIDTH);
            SP_CHAT.setMaxWidth(CHAT_WIDTH);
            SP_CHAT.getChildren().add(sideChat.getChatVBox());
        } else {
            for (double width = SP_CHAT.getWidth(); width >= 0; width--) {
                SP_CHAT.setPrefWidth(width);
                SP_CHAT.setMinWidth(width);
                SP_CHAT.setMaxWidth(width);
            }
            SP_CHAT.getChildren().clear();
        }
    }

    /**
     * Устанавливает состояние наличия новых сообщений.
     *
     * @param hasNewMessages true, если есть новые сообщения, иначе false.
     */
    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages.set(hasNewMessages);
    }

    /**
     * Возвращает свойство состояния новых сообщений.
     *
     * @return BooleanProperty, связанное с наличием новых сообщений.
     */
    public BooleanProperty hasNewMessagesProperty() {
        return hasNewMessages;
    }
}
