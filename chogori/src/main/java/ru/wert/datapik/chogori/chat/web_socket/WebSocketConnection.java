package ru.wert.datapik.chogori.chat.web_socket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebSocketConnection {

    public void create(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();

        WebSocket ws = client.newWebSocket(request, listener);

    }
}
