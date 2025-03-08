package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Message extends _BaseEntity implements Item {

    public enum MessageStatus {
        RECEIVED, //Уведомление получено
        DELIVERED //Сообщение прочитано
    }

    public enum MessageType {
        USER_IN("Пользователь вошел"),
        USER_OUT("Пользователь вышел"),
        CHAT_TEXT("текст"),
        CHAT_DRAFTS("чертежи"),
        CHAT_FOLDERS("комплекты чертежей"),
        CHAT_PICS("изображения"),
        CHAT_PASSPORTS("пасспорта"),
        ADD_DRAFT("Добавление чертежа"),
        UPDATE_DRAFT("Обновление чертежа"),
        DELETE_DRAFT("Удаление чертежа"),
        ADD_FOLDER("Добавление комплекта чертежей"),
        UPDATE_FOLDER("Обновление комплекта чертежей"),
        DELETE_FOLDER("Удаление комплекта чертежей");

        @Getter String typeName;

        MessageType(String typeName) {
            this.typeName = typeName;
        }
    }


    private MessageType type; //Тип сообщения (текстовый, чертеж и т.д.)
    private Room room; //id руппы чата
    private Long senderId; //id пользователя, написавшего в группе
    private String text; //Текст сообщения, либо строку id-шников
    private LocalDateTime creationTime; //Время отправки сообщения
    private MessageStatus status; //Время отправки сообщения

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
