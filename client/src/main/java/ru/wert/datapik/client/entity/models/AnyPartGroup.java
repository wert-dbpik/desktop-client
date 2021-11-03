package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class AnyPartGroup extends _BaseEntity implements Item, Serializable {

    private Long parentId;
    private String name;
//    private List<AnyPart> partsInGroup;


    @Override
    public String toUsefulString() {
        return name;
    }
}
