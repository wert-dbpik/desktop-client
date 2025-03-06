package ru.wert.tubus.chogori.chat.socketwork;

import ru.wert.tubus.client.entity.models.Message;

/**
 * Этот класс отвечает за отправку сообщений, связанных с подключением и отключением пользователей.
 */
public class ServiceMessaging {

    /**
     * Отправляет сообщение о входе пользователя в систему.
     *
     * @param userId Идентификатор пользователя
     */
    public static void sendMessageUserIn(Long userId) {
        Message rookieMessage = new Message();
        rookieMessage.setType(Message.MessageType.USER_IN);
        rookieMessage.setSenderId(userId);
        SocketService.sendMessage(rookieMessage);
    }

    /**
     * Отправляет сообщение о выходе пользователя из системы.
     *
     * @param userId Идентификатор пользователя
     */
    public static void sendMessageUserOut(Long userId) {
        Message rookieMessage = new Message();
        rookieMessage.setType(Message.MessageType.USER_OUT);
        rookieMessage.setSenderId(userId);
        SocketService.sendMessage(rookieMessage);
    }
}
