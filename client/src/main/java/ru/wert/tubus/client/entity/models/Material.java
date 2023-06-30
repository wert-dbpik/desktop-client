package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.CatalogableItem;
import ru.wert.tubus.client.interfaces.Item;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class Material extends _BaseEntity implements Item, CatalogableItem, Serializable {

    private AnyPart anyPart;
    private MaterialGroup catalogGroup; // папки в каталоге
    String name;
    MatType matType;
    String note;
    double paramS; //толщина (t), диаметр (D), периметр P
    double paramX;//плотность, масса пог. м. (Mпог.м)


    //Конструктор необходим для создания узлов дерева
    public Material(String name) {
        super.setId(0L);
        this.anyPart = null;
        this.catalogGroup = null;
        this.name = name;
        this.matType = null;
        this.note = "";
        this.paramS = 0;
        this.paramX = 0;
    }

    @Override
    public void setCatalogGroup(CatalogGroup catalogGroup) {
        this.catalogGroup = (MaterialGroup) catalogGroup;
    }

    @Override
    public String toUsefulString() {
        return name;
    }
}
