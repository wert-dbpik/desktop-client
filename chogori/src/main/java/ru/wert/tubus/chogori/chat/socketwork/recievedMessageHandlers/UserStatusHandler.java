package ru.wert.tubus.chogori.chat.socketwork.recievedMessageHandlers;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceREST.UserService;
import ru.wert.tubus.client.utils.MessageType;

/**
 * Обработчик сообщений о статусе пользователя (вход/выход).
 */
@Slf4j
public class UserStatusHandler {
    public static void handle(Message message, MessageType type, StringBuilder str, RoomsController roomsController) {
        Long userId = message.getSenderId();
        User user = UserService.getInstance().findById(userId);
        String userName = user == null ? "Какой-то инкогнито" : user.getName();
        str.append(type.getTypeName()).append(" ").append(userName);
        roomsController.getUsersOnline().stream()
                .filter(userOnline -> userOnline.getUser().getId().equals(message.getSenderId()))
                .findFirst()
                .ifPresent(userOnline -> {
                    userOnline.setOnline(type == MessageType.USER_IN);
                });
        roomsController.refreshListOfUsers();

    }
}
