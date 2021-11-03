package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.CatalogGroup;

import java.io.Serializable;

/**
 * Класс описывает группы продуктов - элементы каталога изделий,
 * например группа ШКМ в каталоге содержит изделия ШКМ-У1000, ШКМ-У1500А и т.д.
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class ProductGroup extends _BaseEntity implements CatalogGroup, Serializable {

    private String name;
    private Long parentId;

    @Override
    public String toUsefulString() {
        return name;
    }

    /**
     * Конструктор для создания root в дереве
     * @param id
     * @param name
     */
    public ProductGroup(Long id, Long parentId, String name) {
        super.setId(id);
        this.name = name;
    }

}
