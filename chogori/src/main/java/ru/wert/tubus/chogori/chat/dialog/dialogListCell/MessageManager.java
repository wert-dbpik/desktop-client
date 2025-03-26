package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import java.io.IOException;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell.OUT;

@Slf4j
public class MessageManager {

    private final Boolean ONE_TO_ONE_CHAT;

    public MessageManager(Room room) {
        ONE_TO_ONE_CHAT = room.getName().startsWith("one-to-one");
    }

    public VBox formatMessage(Message message, String in_out) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/message.fxml"));
            VBox messageContainer = loader.load();

            // Настройка разделителя
            Separator separator = (Separator) messageContainer.lookup("#separator");
            configureSeparator(separator);

            // Получаем остальные элементы
            Label lblFrom = (Label) messageContainer.lookup("#lblFrom");
            Label lblDate = (Label) messageContainer.lookup("#lblDate");
            Label lblTitle = (Label) messageContainer.lookup("#lblTitle");
            Label lblTime = (Label) messageContainer.lookup("#lblTime");
            VBox vbMessageContainer = (VBox) messageContainer.lookup("#vbMessageContainer");
            VBox vbOutlineMessage = (VBox) messageContainer.lookup("#vbOutlineMessage");
            VBox vbMessage = (VBox) messageContainer.lookup("#vbMessage");

            // Настройка стилей
            lblTitle.setId("messageTitleLabel");
            lblTime.setId("messageTimeLabel");
            lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime().toString()));

            // Настройка отступов сообщения
            configureMessageMargins(vbMessageContainer);

            // Настройка стилей сообщения
            configureMessageStyles(in_out, vbMessageContainer, vbOutlineMessage, vbMessage, lblFrom, lblDate);

            // Установка данных
            setSenderData(message, lblFrom, lblDate);
            renderMessageContent(message, lblTitle, vbOutlineMessage, vbMessage);

            return messageContainer;

        } catch (IOException e) {
            log.error("Error loading message FXML", e);
            return createErrorNode("Failed to load message");
        }
    }

    private void configureSeparator(Separator separator) {
        separator.setVisible(false);
        separator.setManaged(false);
    }

    private void configureMessageMargins(VBox messageContainer) {
        // Уменьшенные отступы вокруг сообщения
        VBox.setMargin(messageContainer, new Insets(2, 0, 2, 0)); // top, right, bottom, left
    }

    private void setSenderData(Message message, Label lblFrom, Label lblDate) {
        try {
            String senderName = "Unknown User";
            if (message.getSenderId() != null) {
                User sender = CH_USERS.findById(message.getSenderId());
                if (sender != null) {
                    senderName = sender.getName();
                }
            }
            lblFrom.setText(senderName);
            lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime().toString()));
        } catch (Exception e) {
            log.error("Error setting sender data", e);
            lblFrom.setText("Error");
            lblDate.setText("");
        }
    }

    private void configureMessageStyles(String in_out, VBox vbMessageContainer,
                                        VBox vbOutlineMessage, VBox vbMessage,
                                        Label lblFrom, Label lblDate) {
        if (in_out.equals(OUT)) {
            vbMessageContainer.getChildren().removeAll(lblFrom, lblDate);
            vbMessageContainer.setAlignment(Pos.TOP_LEFT);
            lblFrom.setId("outMessageDataLabel");
            lblDate.setId("outMessageDataLabel");
            vbOutlineMessage.setId("outOutlineMessageVBox");
            vbMessage.setId("outMessageVBox");
        } else {
            if (ONE_TO_ONE_CHAT) {
                vbMessageContainer.getChildren().removeAll(lblFrom, lblDate);
            }
            vbMessageContainer.setAlignment(Pos.TOP_RIGHT);
            lblFrom.setId("inMessageDataLabel");
            lblDate.setId("inMessageDataLabel");
            vbOutlineMessage.setId("inOutlineMessageVBox");
            vbMessage.setId("inMessageVBox");
        }
    }

    private void renderMessageContent(Message message, Label lblTitle,
                                      VBox vbOutlineMessage, VBox vbMessage) {
        try {
            MessageRenderer renderer = new MessageRenderer(lblTitle);

            switch (message.getType()) {
                case CHAT_TEXT:
                    vbOutlineMessage.getChildren().remove(lblTitle);
                    renderer.mountText(vbMessage, message);
                    break;
                case CHAT_DRAFTS:
                    renderer.mountDrafts(vbMessage, message);
                    break;
                case CHAT_FOLDERS:
                    renderer.mountFolders(vbMessage, message);
                    break;
                case CHAT_PICS:
                    renderer.mountPics(vbMessage, message);
                    break;
                case CHAT_PASSPORTS:
                    renderer.mountPassports(vbMessage, message);
                    break;
            }
        } catch (Exception e) {
            log.error("Error rendering message content", e);
            vbMessage.getChildren().add(new Label("Error displaying content"));
        }
    }

    private VBox createErrorNode(String errorText) {
        VBox errorBox = new VBox();
        errorBox.getChildren().add(new Label(errorText));
        return errorBox;
    }
}
