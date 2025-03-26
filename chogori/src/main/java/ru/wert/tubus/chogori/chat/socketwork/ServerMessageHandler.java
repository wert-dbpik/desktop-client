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
 * Класс обработчик сообщений со стороны сервера.
 * Отвечает за обработку входящих сообщений и их отображение в интерфейсе.
 */
@Slf4j
public class ServerMessageHandler {

    public static RoomsController roomsController; // Контроллер для управления списком комнат и пользователей
    public static DialogController dialogController; // Контроллер для управления списком комнат и пользователей

    /**
     * Обрабатывает входящее сообщение от сервера.
     * @param message Входящее сообщение.
     */
    public static void handle(Message message) {
        log.info(String.format("Message from server received: %s", message.toUsefulString()));
        Platform.runLater(() -> {
            if (SP_NOTIFICATION != null && message != null)
                SP_NOTIFICATION.setText(makeString(message)); // Отображаем уведомление в интерфейсе
        });
    }

    /**
     * Формирует строку для отображения в уведомлении на основе типа сообщения.
     * Если сообщение относится к комнате (тип CHAT_), комната добавляется в список, если она отсутствует.
     *
     * @param message Входящее сообщение.
     * @return Строка для отображения в уведомлении.
     */
    private static String makeString(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder str = new StringBuilder("");
        Message.MessageType type = message.getType();

        // Обработка сообщений о входе/выходе пользователя
        if (type == Message.MessageType.USER_IN || type == Message.MessageType.USER_OUT) {
            str.append(type.getTypeName()).append(" ").append(CH_USERS.findById(message.getSenderId()).getName());
            roomsController.getUsersOnline().stream()
                    .filter(userOnline -> userOnline.getUser().getId().equals(message.getSenderId()))
                    .findFirst()
                    .ifPresent(userOnline -> {
                        userOnline.setOnline(type == Message.MessageType.USER_IN); // Обновляем статус пользователя
                    });
            roomsController.refreshListOfUsers(); // Обновляем список пользователей

            // Обработка сообщений о чертежах
        } else if (type == Message.MessageType.ADD_DRAFT || type == Message.MessageType.UPDATE_DRAFT || type == Message.MessageType.DELETE_DRAFT) {
            try {
                Draft draft = objectMapper.readValue(message.getText(), Draft.class);
                str.append("Пользователь ");
                str.append(draft.getStatusUser().toUsefulString());
                str.append(type.getTypeName()).append(draft.toUsefulString());
            } catch (JsonProcessingException e) {
                // Обработка ошибки парсинга JSON
                System.err.println("Ошибка при парсинге JSON: " + e.getMessage());
            }

            // Обработка push-уведомлений
        } else if (type == Message.MessageType.PUSH) {

            Platform.runLater(()->{
                //Выключаем моргающую кнопку
                Gson gson = GsonConfiguration.createGson();
                ChatStaticMaster.UNREAD_MESSAGES.add(gson.fromJson(message.getText(), Message.class)); //Добавляем сообщение в список непрочитанных сообщений
                ApplicationController.chat.hasNewMessagesProperty().set(true);
            });

//            PushNotification.show(message.getText()); // Отображаем push-уведомление

            // Обработка сообщений чата (текст, чертежи, паспорта и т.д.)
        } else if (type == Message.MessageType.CHAT_TEXT ||
                type == Message.MessageType.CHAT_PASSPORTS ||
                type == Message.MessageType.CHAT_DRAFTS ||
                type == Message.MessageType.CHAT_FOLDERS ||
                type == Message.MessageType.CHAT_PICS) {
            // Проверяем, входит ли пользователь в комнату
            Long roomId = message.getRoomId();
            Room room = RoomService.getInstance().findById(roomId);
            if (room != null && room.getRoommates().contains(CH_CURRENT_USER.getId())) {
                // Добавляем комнату в список, если она отсутствует
                roomsController.addRoomIfAbsent(room);
            }

            // Обрабатываем сообщение чата
            handleChatMessage(message);
        } else if (type == Message.MessageType.UPDATE_MESSAGE){
            updateMessageInOpenRooms(message);
        } else if (type == Message.MessageType.DELETE_MESSAGE){
            deleteMessageFromOpenRooms(message);
        }

        return str.toString();
    }

    /**
     * Обрабатывает сообщение чата и добавляет его в соответствующий диалог.
     * @param message Сообщение чата.
     */
    private static void handleChatMessage(Message message) {
        Room room = RoomService.getInstance().findById(message.getRoomId());
        if (room != null) {
            // Находим диалог для этой комнаты с помощью метода из SideRoomDialogController
            DialogListView dialog = dialogController.findDialogForRoom(room);
            if (dialog != null) {
                // Добавляем сообщение в диалог
                dialog.getItems().add(message);
                dialog.refresh();
                dialog.scrollTo(message);

                // Меняем статус сообщения на DELIVERED
                message.setStatus(Message.MessageStatus.DELIVERED);

                // Обновляем сообщение на сервере
                CH_MESSAGES.update(message);
            }
        }
    }

}
