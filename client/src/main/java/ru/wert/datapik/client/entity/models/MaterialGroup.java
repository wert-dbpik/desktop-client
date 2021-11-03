package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class MaterialGroup extends _BaseEntity implements Item, CatalogGroup, Serializable {

    private Long parentId;
    private String name;

    @Override
    public String toUsefulString() {
        return name;
    }

    /**
     * Конструктор для создания root в дереве
     * @param id
     * @param name
     */
    public MaterialGroup(Long id, Long parentId, String name) {
        super.setId(id);
        this.parentId = parentId;
        this.name = name;
    }
}
