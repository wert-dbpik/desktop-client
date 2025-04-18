package ru.wert.tubus.client.entity.models;

import com.google.gson.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends _BaseEntity implements Item {

    public enum MessageStatus {
        SENT, //Сообщение отравлено
        RECEIVED, // Сообщение получено
        DELIVERED, // Сообщение прочитано
    }

    public enum MessageType {
        HEARTBEAT("Пользователь онлай"),

        USER_IN("Вошел пользователь"),
        USER_OUT("Вышел пользователь"),

        CHAT_TEXT("текст"),
        CHAT_DRAFTS("чертежи"),
        CHAT_FOLDERS("комплекты чертежей"),
        CHAT_PICS("изображения"),
        CHAT_PASSPORTS("пасспорта"),
        CHAT_SEPARATOR("сепаратор с датой"),
        CHAT_UPDATE_TEMP_ID("Обновить временный ID"),
        MESSAGE_DELIVERED("Сообщение доставлено"),

        UPDATE_PASSPORT("Обновление пасспорта"), //Переименование чертежа

        ADD_DRAFT("Добавление чертежа"),
        UPDATE_DRAFT("Обновлен чертеж"),
        DELETE_DRAFT("Удален чертеж"),

        ADD_FOLDER("Добавлен комплект чертежей"),
        UPDATE_FOLDER("Обновлен комплект чертежей"),
        DELETE_FOLDER("Удален комплект чертежей"),

        ADD_PRODUCT("Добавлено изделие"),
        UPDATE_PRODUCT("Обновлено изделие"),
        DELETE_PRODUCT("Удалено изделие"),

        ADD_PRODUCT_GROUP("Добавлена группа изделий"),
        UPDATE_PRODUCT_GROUP("Обновлена группа изделий"),
        DELETE_PRODUCT_GROUP("Удалена группа изделий"),

        PUSH("Уведомление"),
        DELETE_MESSAGE("Удалить сообщение"),
        UPDATE_MESSAGE("Изменить сообщение");

        @Getter
        private final String typeName;

        MessageType(String typeName) {
            this.typeName = typeName;
        }
    }

    private String tempId; //Временный Id для отправки на сервер
    private MessageType type; // Тип сообщения (текстовый, чертеж и т.д.)
    private Long roomId; // id группы чата
    private Long senderId; // id пользователя, написавшего в группе
    private String text; // Текст сообщения, либо строку id-шников
    private LocalDateTime creationTime; // Время отправки сообщения
    private MessageStatus status; // Статус сообщения

    private transient ObjectProperty<MessageStatus> statusProperty;

    public ObjectProperty<MessageStatus> statusProperty() {
        if (statusProperty == null) {
            statusProperty = new SimpleObjectProperty<>(this, "status", status);
        }
        return statusProperty;
    }

    public MessageStatus getStatus() {
        return statusProperty == null ? status : statusProperty.get();
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
        if (statusProperty != null) {
            statusProperty.set(status);
        }
    }

    /**
     * Создает глубокую копию сообщения на основе оригинала
     * @param original оригинальное сообщение для копирования (не должно быть null)
     * @throws IllegalArgumentException если original равен null
     */
    public Message(Message original) {
        if (original == null) {
            throw new IllegalArgumentException("Оригинальное сообщение не может быть null");
        }

        this.id = original.id;
        this.type = original.type;
        this.roomId = original.roomId;
        this.senderId = original.senderId;
        this.text = original.text;
        this.creationTime = original.creationTime;
        this.status = original.status;
    }

    @Override
    public String getName() {
        // НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public String toUsefulString() {
        return "id: " + id + " ,tempId: " + tempId + " ,from: " + senderId + ", type: " + type.name() + " ,message: " + text;
    }

}
