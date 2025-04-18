package ru.wert.tubus.chogori.chat.socketwork;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.utils.MessageType;

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
            userInMessage.setType(MessageType.USER_IN);
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
            userOutMessage.setType(MessageType.USER_OUT);
            userOutMessage.setSenderId(CH_CURRENT_USER.getId());
            SocketService.sendMessage(userOutMessage);
        } catch (Exception e) {
            log.error("Ошибка при отправке USER_OUT сообщения: {}", e.getMessage());
        }
    }

    public static void sendNotificationMessageDelivered(Message message) {
        try {
            Message notification = new Message();
            notification.setType(MessageType.MESSAGE_DELIVERED);
            notification.setSenderId(CH_CURRENT_USER.getId());
            Gson gson = GsonConfiguration.createGson();
            String jsonMessage = gson.toJson(message, Message.class);
            notification.setText(jsonMessage);
            SocketService.sendMessage(notification);
            log.info("Отправлено уведомление о принятом сообщении {}", message.toUsefulString());
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления о полученном сообщении: {}", e.getMessage());
        }
    }
}
