package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class ChatMessageStatus extends _BaseEntity implements Item {

    private ChatMessage message; //Сообщение
    private User user; //Пользователь, кому написано сообщение
    private Integer status = 0; //Статус сообщения


    @Override
    public String getName() {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public String toUsefulString() {
        return "for: " + user + "Message: " + message + "Status: " + status;
    }
}
