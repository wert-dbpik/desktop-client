package ru.wert.tubus.chogori.chat.socketwork;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.chogori.chat.socketwork.messageHandlers.*;
import ru.wert.tubus.client.entity.models.*;

import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.deleteMessageFromOpenRooms;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.updateMessageInOpenRooms;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_NOTIFICATION;

/**
 * Основной класс-менеджер для обработки сообщений от сервера.
 * Делегирует обработку специализированным классам в зависимости от типа сообщения.
 */
@Slf4j
public class ServerMessageHandler {

    public static RoomsController roomsController;
    public static DialogController dialogController;

    /**
     * Обрабатывает входящее сообщение от сервера.
     * @param message Входящее сообщение.
     */
    public static void handle(Message message) {
        log.info(String.format("Message from server received: %s", message.toUsefulString()));

        Platform.runLater(() -> {
            if (SP_NOTIFICATION != null && message != null)
                SP_NOTIFICATION.setText(processMessage(message));
        });
    }

    /**
     * Определяет тип сообщения и делегирует обработку соответствующему обработчику.
     * @param message Входящее сообщение.
     * @return Строка для отображения в уведомлении.
     */
    private static String processMessage(Message message) {
        Message.MessageType type = message.getType();
        StringBuilder str = new StringBuilder();

        switch (type) {
            case USER_IN:
            case USER_OUT:
                UserStatusHandler.handle(message, type, str, roomsController);
                break;

            case CHAT_TEXT:
            case CHAT_PASSPORTS:
            case CHAT_DRAFTS:
            case CHAT_FOLDERS:
            case CHAT_PICS:
                ChatMessageHandler.handle(message);
                break;

            case UPDATE_MESSAGE:
                updateMessageInOpenRooms(message);
                break;

            case DELETE_MESSAGE:
                deleteMessageFromOpenRooms(message);
                break;

            case ADD_DRAFT:
            case UPDATE_DRAFT:
            case DELETE_DRAFT:
                DraftMessageHandler.handle(message, type, str);
                break;

            case ADD_FOLDER:
            case UPDATE_FOLDER:
            case DELETE_FOLDER:
                FolderMessageHandler.handle(message, type, str);
                break;

            case ADD_PRODUCT:
            case UPDATE_PRODUCT:
            case DELETE_PRODUCT:
                ProductMessageHandler.handle(message, type, str);
                break;

            case PRODUCT_GROUP_CHANGED:
                OtherChangesHandler.handle();
                break;


            case PUSH:
                PushMessageHandler.handle(message);
                break;
        }

        return str.toString();
    }
}
