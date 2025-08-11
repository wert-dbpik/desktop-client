package ru.wert.tubus.client.retrofit;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Формат для сериализации (оставляем как было)
    private static final DateTimeFormatter SERIALIZE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Форматы для десериализации (добавляем поддержку ISO формата)
    private static final DateTimeFormatter[] DESERIALIZE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), // Основной формат
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,             // Формат с 'T' (2025-08-11T11:01:24)
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS") // Формат с миллисекундами
    };

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Сериализация остается без изменений
        return new JsonPrimitive(src.format(SERIALIZE_FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String dateTimeString = json.getAsString();

        // Удаляем 'T' если есть и обрезаем временную зону
        dateTimeString = dateTimeString.replace('T', ' ').split("\\+")[0];

        // Пробуем все форматы по очереди
        for (DateTimeFormatter formatter : DESERIALIZE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            } catch (Exception e) {
                continue; //Следующий формат
            }
        }

        throw new JsonParseException("Не удалось распарсить LocalDateTime: " + json.getAsString());
    }
}