package ru.wert.tubus.chogori.chat.socketwork.messageHandler;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.client.entity.models.Message;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;

/**
 * Обработчик сообщений о статусе пользователя (вход/выход).
 */
@Slf4j
public class UserStatusHandler {
    public static void handle(Message message, Message.MessageType type, StringBuilder str, RoomsController roomsController) {
        str.append(type.getTypeName()).append(" ").append(CH_USERS.findById(message.getSenderId()).getName());
        roomsController.getUsersOnline().stream()
                .filter(userOnline -> userOnline.getUser().getId().equals(message.getSenderId()))
                .findFirst()
                .ifPresent(userOnline -> {
                    userOnline.setOnline(type == Message.MessageType.USER_IN);
                });
        roomsController.refreshListOfUsers();
    }
}
