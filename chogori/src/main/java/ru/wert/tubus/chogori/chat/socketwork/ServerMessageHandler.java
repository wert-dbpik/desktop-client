package ru.wert.tubus.chogori.chat.socketwork;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.app_window.ApplicationController;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.MessageContextMenu;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.chogori.chat.socketwork.messageHandler.*;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.client.entity.models.*;
import ru.wert.tubus.client.entity.serviceREST.RoomService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.deleteMessageFromOpenRooms;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.updateMessageInOpenRooms;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
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
        if (SP_NOTIFICATION == null || message == null) return;
        log.info(String.format("Message from server received: %s", message.toUsefulString()));
        Platform.runLater(() -> {
            Message.MessageType type = message.getType();
            boolean isAdmin = CH_CURRENT_USER.getUserGroup().isAdministrate();
            boolean isUserInOut = type.equals(Message.MessageType.USER_IN) ||
                    type.equals(Message.MessageType.USER_OUT);

            //Сообщения о входе и выходе пользователя из приложения касаются только администратора
            if(!isUserInOut)
                SP_NOTIFICATION.setText(processMessage(message));
            else {
                if (isAdmin)
                    SP_NOTIFICATION.setText(processMessage(message));
            }
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

            case PUSH:
                PushMessageHandler.handle(message);
                break;
        }

        return str.toString();
    }
}
