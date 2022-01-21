package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.CatalogableItem;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.utils.BLConst;

import javax.xml.soap.Detail;
import java.io.Serializable;
import java.util.Set;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"decNumber"}, callSuper = false)
public class Folder extends _BaseEntity implements Item, Comparable<Folder>, Serializable {

    private ProductGroup productGroup;
    private String decNumber;
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
        return decNumber + ", " + name;
    }
}
