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
@EqualsAndHashCode(of = {"user", "text"}, callSuper = false)
public class AppLog extends _BaseEntity implements Item {

    private String time;
    private User user;
    private String text;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String toUsefulString() {
        return text;
    }
}
