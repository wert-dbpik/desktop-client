package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.utils.BLConst;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"productGroup", "name"}, callSuper = false)
public class Folder extends _BaseEntity implements Item, Comparable<Folder>, Serializable {

    private ProductGroup productGroup;
    private String name;
    private String note;

    @Override
    public int compareTo(@NotNull Folder o) {
        if(o.getName().equals(BLConst.RAZLOZHENO))
            return 0;
        return
                toUsefulString().toLowerCase().compareTo(o.toUsefulString().toLowerCase());
    }


    @Override
    public String toUsefulString() {
        return name;
    }
}
