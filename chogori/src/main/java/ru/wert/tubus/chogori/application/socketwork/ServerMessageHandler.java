package ru.wert.tubus.chogori.application.socketwork;

import lombok.extern.slf4j.Slf4j;

/**
 * Класс обработчик сообщений со стороны сервера
 */
@Slf4j
public class ServerMessageHandler {

    public void handle(String message){
        log.debug(String.format("Message form server received: %s", message));
    }
}
