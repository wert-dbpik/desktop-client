package ru.wert.tubus.client.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonConfiguration {

    public static Gson createGson(){
        return
                new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .setLenient()
                        .create();
    }
}
