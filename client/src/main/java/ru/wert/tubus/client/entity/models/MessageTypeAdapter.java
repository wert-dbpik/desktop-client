package ru.wert.tubus.client.entity.models;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.wert.tubus.client.utils.MessageStatus;
import ru.wert.tubus.client.utils.MessageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageTypeAdapter extends TypeAdapter<Message> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void write(JsonWriter out, Message message) throws IOException {
        if (message == null) {
            out.nullValue();
            return;
        }

        out.beginObject();

        // Базовые поля из _BaseEntity
        if (message.getId() != null) {
            out.name("id").value(message.getId());
        }

        // Остальные поля Message
        writeField(out, "tempId", message.getTempId());
        writeField(out, "type", message.getType() != null ? message.getType().name() : null);
        writeField(out, "roomId", message.getRoomId());
        writeField(out, "senderId", message.getSenderId());
        writeField(out, "text", message.getText());
        writeField(out, "creationTime", message.getCreationTime() != null
                ? message.getCreationTime().format(DATE_TIME_FORMATTER) : null);
        writeField(out, "status", message.getStatus() != null ? message.getStatus().name() : null);

        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Message message = new Message();
        in.beginObject();

        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "id":
                    if (in.peek() != JsonToken.NULL) {
                        message.setId(in.nextLong());
                    } else {
                        in.nextNull();
                    }
                    break;

                case "tempId":
                    if (in.peek() != JsonToken.NULL) {
                        message.setTempId(in.nextString());
                    } else {
                        in.nextNull();
                    }
                    break;

                case "type":
                    if (in.peek() != JsonToken.NULL) {
                        message.setType(MessageType.valueOf(in.nextString()));
                    } else {
                        in.nextNull();
                    }
                    break;

                case "roomId":
                    if (in.peek() != JsonToken.NULL) {
                        message.setRoomId(in.nextLong());
                    } else {
                        in.nextNull();
                    }
                    break;

                case "senderId":
                    if (in.peek() != JsonToken.NULL) {
                        message.setSenderId(in.nextLong());
                    } else {
                        in.nextNull();
                    }
                    break;

                case "text":
                    if (in.peek() != JsonToken.NULL) {
                        message.setText(in.nextString());
                    } else {
                        in.nextNull();
                    }
                    break;

                case "creationTime":
                    if (in.peek() != JsonToken.NULL) {
                        message.setCreationTime(LocalDateTime.parse(in.nextString(), DATE_TIME_FORMATTER));
                    } else {
                        in.nextNull();
                    }
                    break;

                case "status":
                    if (in.peek() != JsonToken.NULL) {
                        message.setStatus(MessageStatus.valueOf(in.nextString()));
                    } else {
                        in.nextNull();
                    }
                    break;

                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();
        return message;
    }

    private void writeField(JsonWriter out, String name, String value) throws IOException {
        if (value != null) {
            out.name(name).value(value);
        }
    }

    private void writeField(JsonWriter out, String name, Long value) throws IOException {
        if (value != null) {
            out.name(name).value(value);
        }
    }

    private void writeField(JsonWriter out, String name, LocalDateTime value) throws IOException {
        if (value != null) {
            out.name(name).value(value.format(DATE_TIME_FORMATTER));
        }
    }
}
