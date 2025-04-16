package ru.wert.tubus.chogori.chat.socketwork.recievedMessageHandlers;

import com.google.gson.Gson;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.app_window.ApplicationController;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

/**
 * Обработчик push-уведомлений.
 */
@Slf4j
public class PushMessageHandler {
    public static void handle(Message message) {
        Platform.runLater(() -> {
            Gson gson = GsonConfiguration.createGson();
            Message unreadMessage = gson.fromJson(message.getText(), Message.class);
            if(!ChatStaticMaster.UNREAD_MESSAGES.contains(unreadMessage))
                ChatStaticMaster.UNREAD_MESSAGES.add(unreadMessage);
            ApplicationController.chat.hasNewMessagesProperty().set(true);
        });
    }
}
