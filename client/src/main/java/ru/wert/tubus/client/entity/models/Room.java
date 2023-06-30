package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class Room extends _BaseEntity implements Item {

    private String name; //заголовок чата, его название
    private User creator; //id пользователя создавшего чат
    private boolean editable; //Можно изменять список пользователей
    private List<User> roommates;


    @Override
    public String toUsefulString() {
        return name;
    }
}
