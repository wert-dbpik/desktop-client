package ru.wert.tubus.chogori.chat.socketwork;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;

import java.time.LocalDateTime;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Этот класс отвечает за отправку сообщений, связанных с подключением и отключением пользователей.
 */
@Slf4j
public class ServiceMessaging {

    /**
     * Отправляет сообщение о входе пользователя в систему.
     */
    public static void sendMessageUserIn(Long userId) {
        try {
            Message userInMessage = new Message();
            userInMessage.setType(Message.MessageType.USER_IN);
            userInMessage.setSenderId(userId);
            SocketService.sendMessage(userInMessage);
        } catch (Exception e) {
            log.error("Ошибка при отправке USER_IN сообщения: {}", e.getMessage());
        }
    }

    /**
     * Отправляет сообщение о выходе пользователя из системы.

     */
    public static void sendMessageUserOut() {
        try {
            Message userOutMessage = new Message();
            userOutMessage.setType(Message.MessageType.USER_OUT);
            userOutMessage.setSenderId(CH_CURRENT_USER.getId());
            SocketService.sendMessage(userOutMessage);
        } catch (Exception e) {
            log.error("Ошибка при отправке USER_OUT сообщения: {}", e.getMessage());
        }
    }
}
