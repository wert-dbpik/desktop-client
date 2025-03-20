package ru.wert.tubus.client.entity.models;

import com.google.gson.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends _BaseEntity implements Item {

    public enum MessageStatus {
        RECEIVED, // Уведомление получено
        DELIVERED // Сообщение прочитано
    }

    public enum MessageType {
        USER_IN("Вошел пользователь"),
        USER_OUT("Вышел пользователь"),
        CHAT_TEXT("текст"),
        CHAT_DRAFTS("чертежи"),
        CHAT_FOLDERS("комплекты чертежей"),
        CHAT_PICS("изображения"),
        CHAT_PASSPORTS("пасспорта"),
        CHAT_SEPARATOR("сепаратор с датой"),
        ADD_DRAFT("Добавление чертежа"),
        UPDATE_DRAFT("Обновлен чертеж"),
        DELETE_DRAFT("Удален чертеж"),
        ADD_FOLDER("Добавлен комплект чертежей"),
        UPDATE_FOLDER("Обновлен комплект чертежей"),
        DELETE_FOLDER("Удален комплект чертежей"),
        PUSH("Уведомление");

        @Getter
        private final String typeName;

        MessageType(String typeName) {
            this.typeName = typeName;
        }
    }

    private MessageType type; // Тип сообщения (текстовый, чертеж и т.д.)
    private Long roomId; // id группы чата
    private Long senderId; // id пользователя, написавшего в группе
    private String text; // Текст сообщения, либо строку id-шников
    private LocalDateTime creationTime; // Время отправки сообщения
    private MessageStatus status; // Статус сообщения

    @Override
    public String getName() {
        // НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public String toUsefulString() {
        return "from: " + senderId + ", type: " + type.name() + " ,message: " + text;
    }

}
