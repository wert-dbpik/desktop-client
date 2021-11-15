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
public class UserGroup extends _BaseEntity implements Item {

    private String name;
    private boolean administrate;
    private boolean editUsers;
    private boolean editDrafts;
    private boolean commentDrafts;
    private boolean editProducts;
    private boolean editMaterials;


    @Override
    public String toUsefulString() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
