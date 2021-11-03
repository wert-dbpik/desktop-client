package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.utils.BLConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"prefix", "number"}, callSuper = false)
public class Passport extends _BaseEntity implements Item  {

    private Prefix prefix;
    private String number;
    private String name;
    private List<Long> draftIds = new ArrayList<>(0);

    @Override
    public String toUsefulString() {
        String body = number + BLConst.SEPARATOR + name;
        if(prefix.getName().equals("-"))
            return body;
        else
            return prefix.getName() + "." + body;
    }


    @Override
    public String toString() {

        return "Passport{" +
                ", id=" + id +
                "prefix=" + ((prefix == null) ? null : prefix.getName()) +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
