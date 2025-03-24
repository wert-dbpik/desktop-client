package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import java.io.IOException;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;

@Slf4j
public class DialogListCell extends ListCell<Message> {



    static final String OUT = "message_out"; // Стиль для исходящих сообщений
    static final String IN = "message_in";   // Стиль для входящих сообщений

    private final MessageManager messageManager;

    public DialogListCell(Room room) {
        this.messageManager = new MessageManager(room);
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            Parent mes;
            if (message.getType().equals(Message.MessageType.CHAT_SEPARATOR)) {
                mes = MessageRenderer.mountSeparator(message);
            } else {
                // Определяем, является ли сообщение исходящим или входящим
                if (message.getSenderId() != null && message.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId())) {
                    mes = messageManager.formatMessage(message, OUT);
                } else {
                    mes = messageManager.formatMessage(message, IN);
                }
            }

            setGraphic(mes);
        }
    }



}
