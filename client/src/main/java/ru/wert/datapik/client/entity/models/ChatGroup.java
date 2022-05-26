package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class ChatGroup extends _BaseEntity implements Item {

    private String name; //заголовок чата, его название
    private User user ; //id пользователя создавшего чат


    @Override
    public String toUsefulString() {
        return name;
    }
}
