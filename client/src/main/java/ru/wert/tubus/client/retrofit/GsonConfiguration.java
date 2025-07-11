package ru.wert.tubus.client.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.MessageTypeAdapter;

import java.time.LocalDateTime;

public class GsonConfiguration {

    public static Gson createGson(){
        return
                new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .registerTypeAdapter(Message.class, new MessageTypeAdapter())
                        .setLenient()
                        .create();
    }
}
