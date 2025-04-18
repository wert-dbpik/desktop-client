package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;

import java.io.IOException;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell.IN;
import static ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell.OUT;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_DELIVERED_IMG;

@Slf4j
public class MessageCardsManager {

    private final Boolean ONE_TO_ONE_CHAT; // Индивидуальный чат, не групповой
    private Message currentMessage;

    private VBox vbMessageContainer; // Самый верхний контейнер для сообщения
    private VBox vbOutlineMessage;   // Контейнер, включающий заголовок, сообщение и время создания
    private VBox vbMessage;          // Контейнер для самого сообщения
    private Label lblFrom;           // Метка для отображения имени отправителя
    private Label lblDate;           // Метка для отображения даты сообщения
    private Label lblTitle;          // Метка для отображения заголовка сообщения
    private HBox hbStatus;           // Строка статус, где время и статус
    private Label lblTime;           // Метка для отображения времени сообщения
    private ImageView imgStatus;         // Метка о статусе сообщения

    private Separator separator; // Разделитель между сообщениями

    public MessageCardsManager(Room room) {
        ONE_TO_ONE_CHAT = room.getName().startsWith("one-to-one");
    }

    /**
     * Форматирует сообщение в зависимости от его типа (входящее или исходящее).
     *
     * @param message Сообщение для форматирования.
     * @param in_out  Тип сообщения (входящее или исходящее).
     * @return Отформатированное сообщение в виде VBox.
     */
    public VBox formatMessage(Message message, String in_out) {
        VBox inMessage = null;
        try {
            // Загружаем шаблон сообщения из FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/message.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            separator.setVisible(false); // Скрываем разделитель

            // Инициализируем элементы интерфейса
            lblFrom = (Label) inMessage.lookup("#lblFrom");
            lblDate = (Label) inMessage.lookup("#lblDate");
            lblTitle = (Label) inMessage.lookup("#lblTitle");
            hbStatus = (HBox) inMessage.lookup("#hbStatus");
            lblTime = (Label) inMessage.lookup("#lblTime");
            lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime().toString()));
            imgStatus = (ImageView) inMessage.lookup("#imgStatus");


            if(in_out.equals(IN)){
                hbStatus.getChildren().remove(imgStatus);
            } else {
                if(message.getStatus().equals(Message.MessageStatus.DELIVERED))
                    imgStatus.setImage(CHAT_DELIVERED_IMG);
            }

            vbMessageContainer = (VBox) inMessage.lookup("#vbMessageContainer");
            vbOutlineMessage = (VBox) inMessage.lookup("#vbOutlineMessage");
            vbMessage = (VBox) inMessage.lookup("#vbMessage");

            lblTitle.setId("messageTitleLabel");
            lblTime.setId("messageTimeLabel");

            MessageCardsRenderer messageCardsRenderer = new MessageCardsRenderer(lblTitle);

            // Настройка стилей для исходящих и входящих сообщений
            if (in_out.equals(OUT)) {
                vbMessageContainer.getChildren().removeAll(lblFrom);
                vbMessageContainer.getChildren().removeAll(lblDate);
                vbMessageContainer.setAlignment(Pos.TOP_RIGHT);
                lblFrom.setId("outMessageDataLabel");
                lblDate.setId("outMessageDataLabel");
                vbOutlineMessage.setId("outOutlineMessageVBox");
                vbMessage.setId("outMessageVBox");
            } else {
                if (ONE_TO_ONE_CHAT) {
                    vbMessageContainer.getChildren().removeAll(lblFrom);
                    vbMessageContainer.getChildren().removeAll(lblDate);
                }
                vbMessageContainer.setAlignment(Pos.TOP_LEFT);
                lblFrom.setId("inMessageDataLabel");
                lblDate.setId("inMessageDataLabel");
                vbOutlineMessage.setId("inOutlineMessageVBox");
                vbMessage.setId("inMessageVBox");
            }

            // Обновляем интерфейс в потоке JavaFX
            Platform.runLater(() -> {
                vbMessage.autosize();

                lblFrom.setText(CH_USERS.findById(message.getSenderId()).getName());
                lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime().toString()));

                // В зависимости от типа сообщения вызываем соответствующий метод для отображения
                switch (message.getType()) {
                    case CHAT_TEXT:
                        vbOutlineMessage.getChildren().removeAll(lblTitle);
                        messageCardsRenderer.mountText(vbMessage, message);
                        break;
                    case CHAT_DRAFTS:
                        messageCardsRenderer.mountDrafts(vbMessage, message);
                        break;
                    case CHAT_FOLDERS:
                        messageCardsRenderer.mountFolders(vbMessage, message);
                        break;
                    case CHAT_PICS:
                        messageCardsRenderer.mountPics(vbMessage, message);
                        break;
                    case CHAT_PASSPORTS:
                        messageCardsRenderer.mountPassports(vbMessage, message);
                        break;
                }
            });

        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML для сообщения: {}", e.getMessage(), e);
            return null;
        }

        this.currentMessage = message;
        message.statusProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateMessageStatus(newVal);
            }
        });

        return inMessage;
    }

    public void updateMessageStatus(Message.MessageStatus status) {
        Platform.runLater(() -> {
            if (imgStatus == null) {
                log.warn("ImageView для статуса сообщения не инициализирован");
                return;
            }

            switch (status) {
                case DELIVERED:
                    imgStatus.setImage(CHAT_DELIVERED_IMG);
                    imgStatus.setVisible(true);
                    break;
                default:
                    imgStatus.setVisible(false);
            }

            if (hbStatus != null) {
                hbStatus.requestLayout();
            }
        });
    }

}
