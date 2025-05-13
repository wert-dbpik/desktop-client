package ru.wert.tubus.client.entity.models;

import javafx.beans.property.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.utils.MessageStatus;
import ru.wert.tubus.client.utils.MessageType;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class Message extends _BaseEntity implements Item {

    private final StringProperty tempId = new SimpleStringProperty();
    private final ObjectProperty<MessageType> type = new SimpleObjectProperty<>();
    private final LongProperty roomId = new SimpleLongProperty();
    private final LongProperty senderId = new SimpleLongProperty();
    private final StringProperty text = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> creationTime = new SimpleObjectProperty<>();
    private final ObjectProperty<MessageStatus> status = new SimpleObjectProperty<>(MessageStatus.SENT);

    // Property getters
    public StringProperty tempIdProperty() { return tempId; }
    public ObjectProperty<MessageType> typeProperty() { return type; }
    public LongProperty roomIdProperty() { return roomId; }
    public LongProperty senderIdProperty() { return senderId; }
    public StringProperty textProperty() { return text; }
    public ObjectProperty<LocalDateTime> creationTimeProperty() { return creationTime; }
    public ObjectProperty<MessageStatus> statusProperty() { return status; }

    // Traditional getters
    public String getTempId() { return tempId.get(); }
    public MessageType getType() { return type.get(); }
    public long getRoomId() { return roomId.get(); }
    public long getSenderId() { return senderId.get(); }
    public String getText() { return text.get(); }
    public LocalDateTime getCreationTime() { return creationTime.get(); }
    public MessageStatus getStatus() { return status.get(); }

    // Traditional setters
    public void setTempId(String tempId) { this.tempId.set(tempId); }
    public void setType(MessageType type) { this.type.set(type); }
    public void setRoomId(long roomId) { this.roomId.set(roomId); }
    public void setSenderId(long senderId) { this.senderId.set(senderId); }
    public void setText(String text) { this.text.set(text); }
    public void setCreationTime(LocalDateTime creationTime) { this.creationTime.set(creationTime); }
    public void setStatus(MessageStatus status) { this.status.set(status); }

    /**
     * Создает глубокую копию сообщения на основе оригинала
     * @param original оригинальное сообщение для копирования (не должно быть null)
     * @throws IllegalArgumentException если original равен null
     */
    public Message(Message original) {
        if (original == null) {
            throw new IllegalArgumentException("Оригинальное сообщение не может быть null");
        }

        this.id = original.getId();
        this.type.set(original.getType());
        this.roomId.set(original.getRoomId());
        this.senderId.set(original.getSenderId());
        this.text.set(original.getText());
        this.creationTime.set(original.getCreationTime());
        this.status.set(original.getStatus());
        this.tempId.set(original.getTempId());
    }

    @Override
    public String getName() {
        // НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public String toUsefulString() {
        return String.format("id: %d, tempId: %s, from: %d, type: %s, message: %s",
                getId(), getTempId(), getSenderId(), getType().name(), getText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;

        // Приоритет ID над временным ID
        if (getId() != null && message.getId() != null) {
            return getId().equals(message.getId());
        }

        // Сравнение по временному ID только если оба имеют tempId
        if (getTempId() != null && message.getTempId() != null) {
            return getTempId().equals(message.getTempId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (getId() != null) return getId().hashCode();
        if (getTempId() != null) return getTempId().hashCode();
        return 0;
    }
}
