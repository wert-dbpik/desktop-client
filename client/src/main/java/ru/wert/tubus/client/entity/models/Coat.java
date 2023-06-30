package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class Coat extends _BaseEntity implements Item {

    private String name;
    private String ral;
    private String note;


    @Override
    public String toUsefulString() {
        return name;
    }
}
