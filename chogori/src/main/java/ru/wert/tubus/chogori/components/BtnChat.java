package ru.wert.tubus.chogori.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController.*;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.getSecondUserInOneToOneChat;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_WHITE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_YELLOW_IMG;
import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_CHAT;
@Slf4j
public class BtnChat{

    private final ImageView imageWhite = new ImageView(CHAT_WHITE_IMG); // Изображение кнопки в обычном состоянии (белый чат)
    private final ImageView imageYellow = new ImageView(CHAT_YELLOW_IMG); // Изображение кнопки в активном состоянии (желтый чат)
    private final BooleanProperty hasNewMessages = new SimpleBooleanProperty(false); // Состояние наличия новых сообщений
    private final Timeline timeline; // Анимация мигания
    public static boolean CHAT_OPEN;
    private SideChat sideChat = new SideChat();

    @Getter
    private Button btnChat;

    /**
     * Конструктор кнопки чата.
     *
     */
    public BtnChat(Button button) {
        this.btnChat = button;


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
     * Проверяет, открыт ли чат в данный момент
     * @return true если чат открыт, false если закрыт
     */
    public boolean isChatOpen() {
        return CHAT_OPEN;
    }

    /**
     * ОТКРЫТЬ ЧАТ
     */
    public void openChat() {
        hasNewMessages.set(false); // Сбрасываем состояние новых сообщений
        CHAT_OPEN = !CHAT_OPEN;
        if (CHAT_OPEN) {
            SP_CHAT.setPrefWidth(CHAT_WIDTH);
            SP_CHAT.setMinWidth(CHAT_WIDTH);
            SP_CHAT.setMaxWidth(CHAT_WIDTH);
            SP_CHAT.getChildren().add(sideChat.getChatVBox());

            // Проверяем, есть ли открытый диалог в SideChat
            DialogController dialogController = sideChat.getDialogController();
            if (dialogController != null && sideChat.getMainChatPane().getChildren().contains(sideChat.getChatDialog())) {
                DialogListView currentDialog = dialogController.getDialogListView();
                if (currentDialog != null) {
                    Platform.runLater(()->currentDialog.refresh());
                    openOneRoom(currentDialog);
                    Room room = currentDialog.getRoom();
                    User secondUser = getSecondUserInOneToOneChat(room);

                    // Запускаем обработку неотправленных сообщений в отдельном потоке
                    new Thread(() -> {
                        List<Message> undeliveredMessages = CH_MESSAGES.findUndeliveredByRoomAndUser(room, secondUser.getId());
                        for (Message m : undeliveredMessages) {
                            m.setStatus(Message.MessageStatus.DELIVERED);
                            CH_MESSAGES.update(m);
                            ServiceMessaging.sendNotificationMessageDelivered(m);
                        }
                    }).start();
                }
            }
        } else {
            closeAllRooms(); //Map DialogController.openRooms сбрасываем все комнаты в false
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
