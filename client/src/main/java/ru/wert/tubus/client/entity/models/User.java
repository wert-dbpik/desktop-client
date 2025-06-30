package ru.wert.tubus.client.entity.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class User extends _BaseEntity implements Item {

    private String name;
    private String password;
    private UserGroup userGroup;
    private boolean logging; //следует ли пользователя логировать
    private boolean active; //активный ли пользователь
    private boolean online; //пользователь online

    @Override
    public String toUsefulString() {
        return name;
    }
}
