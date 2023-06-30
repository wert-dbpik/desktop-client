package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.utils.BLConst;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "secondName"}, callSuper = false)
public class AnyPart extends _BaseEntity implements Item {

    private String name;
    private String secondName;
    private AnyPartGroup anyPartGroup;

    @Override
    public String toUsefulString() {
        if(secondName == null)
            return name;
        return name + BLConst.SEPARATOR + secondName;
    }

    @Override
    public String toString() {
        return "AnyPart{" +
                ", id=" + id +
                "name='" + name + '\'' +
                ", secondName='" + secondName + '\'' +
                ", anyPartGroup=" + anyPartGroup +
                '}';
    }
}
