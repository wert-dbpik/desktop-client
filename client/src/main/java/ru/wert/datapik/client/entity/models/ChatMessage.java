package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class ChatMessage extends _BaseEntity implements Item {

    private ChatGroup group; //id руппы чата
    private User user; //id пользователя, написавшего в группе
    private Integer messageType; //тип ообщения, см. EMessageType
    private String text; //Текст сообщения, либо строку id-шников
    private String creationTime; //Время отправки сообщения

    @Override
    public String getName() {
        // НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public String toUsefulString() {
        return "from: " + user.getName() + " ,type: " + messageType +" ,message: " + text;
    }
}
