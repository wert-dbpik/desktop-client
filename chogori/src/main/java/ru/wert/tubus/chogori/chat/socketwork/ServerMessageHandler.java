package ru.wert.tubus.chogori.chat.socketwork;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;

/**
 * Класс обработчик сообщений со стороны сервера
 */
@Slf4j
public class ServerMessageHandler {

    public static void handle(Message message){
        log.debug(String.format("Message from server received: %s", message));
    }
}
