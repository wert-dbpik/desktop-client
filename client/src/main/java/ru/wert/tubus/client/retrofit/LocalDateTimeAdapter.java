package ru.wert.tubus.client.retrofit;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Формат даты и времени
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Сериализация: LocalDateTime -> строка
        return new JsonPrimitive(src.format(FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            String dateTimeString = json.getAsString();
            return LocalDateTime.parse(dateTimeString, FORMATTER);
        } catch (Exception e) {
            throw new JsonParseException("Не удалось распарсить LocalDateTime: " + json.getAsString(), e);
        }
    }
}