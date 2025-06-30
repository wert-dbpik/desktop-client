package ru.wert.tubus.client.entity.models.extra;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskPasswordConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        // Маскируем пароль в JSON-подобных структурах
        message = message.replaceAll("\"password\"\\s*:\\s*\"[^\"]+\"", "\"password\":\"***\"");
        // Маскируем пароль в обычных строках (например, в toString())
        message = message.replaceAll("password\\s*=\\s*'[^']+'", "password='***'");
        message = message.replaceAll("password\\s*=\\s*\"[^\"]+\"", "password=\"***\"");
        return message;
    }
}
