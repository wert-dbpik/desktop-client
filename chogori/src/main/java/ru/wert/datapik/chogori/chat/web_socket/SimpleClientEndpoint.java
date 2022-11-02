package ru.wert.datapik.chogori.chat.web_socket;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

@Slf4j
public class SimpleClientEndpoint extends Endpoint {


    @Override
    public void onOpen(Session session, EndpointConfig config) {

        log.info("session opened");

        session.addMessageHandler(String.class, new MessageHandler.Whole() {
            @Override
            public void onMessage(Object text) {
                log.info("recieved message from server: `{}`", text);
            }
        });

        String message = "Hello from client";

        log.info("trying to send message `{}` to server...", message);

        try {
            session.getBasicRemote().sendText(message);
            log.info("message sent successfully");
        } catch (IOException e) {
            log.error("error sending message to server", e);
        }

    }

}
