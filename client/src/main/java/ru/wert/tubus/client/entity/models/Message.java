package ru.wert.tubus.client.entity.models;

import com.google.gson.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.utils.MessageStatus;
import ru.wert.tubus.client.utils.MessageType;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends _BaseEntity implements Item {

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
