package ru.wert.tubus.client.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Roommate extends _BaseEntity implements Item {

    private Room room; //ID комнаты
    private Long userId; // ID пользователя
    private boolean visibleForUser; // Видимость чата для пользователя
    private boolean member; // Участие пользователя в чате

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toUsefulString() {
        return null;
    }
}
