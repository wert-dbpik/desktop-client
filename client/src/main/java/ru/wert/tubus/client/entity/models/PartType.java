package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class PartType extends _BaseEntity implements Item, Serializable {

    private Long parentId;
    private String name;
    private List<AnyPart> partsInGroup;


    @Override
    public String toUsefulString() {
        return name;
    }
}
