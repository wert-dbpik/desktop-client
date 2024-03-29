package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "text"}, callSuper = false)
public class AppLog extends _BaseEntity implements Item {

    private String time;
    private boolean adminOnly;
    private User user;
    private Integer application;
    private String version;
    private String text;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String toUsefulString() {
        return user.getName() + ": " + text;
    }
}
