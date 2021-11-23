package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class VersionServer extends _BaseEntity implements Item {

    private String data;
    private String name;
    private String note;

    @Override
    public String toUsefulString() {
        return name;
    }
}
