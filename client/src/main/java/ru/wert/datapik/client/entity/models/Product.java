package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.CatalogableItem;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.utils.BLConst;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"passport"}, callSuper = false)
public class Product extends _BaseEntity implements CatalogableItem, Comparable<Product>, Serializable {

    private AnyPart anyPart;
    private ProductGroup productGroup; // папки в каталоге
    private Passport passport;
    private String variant;
    private Folder folder; //Реальная папка в архиве
    private String initialExcelName;
    private String note;

    @Override
    public CatalogGroup getCatalogGroup() {
        return productGroup;
    }

    @Override
    public void setCatalogGroup(CatalogGroup catalogGroup) {
        this.productGroup = (ProductGroup) catalogGroup;
    }

    @Override
    public int compareTo(@NotNull Product o) {
        if(o.getPassport().getName().equals(BLConst.RAZNOE)) return 0;
        return toUsefulString().toLowerCase().compareTo(o.toUsefulString().toLowerCase());
    }

    @Override
    public String getName() {
        return anyPart.getSecondName();
    }

    @Override
    public String toUsefulString() {
        if (super.id == 1L) return BLConst.RAZNOE;
        if (variant == null || variant.isEmpty())
            return passport.toUsefulString();
        else
            return passport.toUsefulString() + "-" + variant;
    }

    @Override
    public String toString() {
        return "Product{" +
                "anyPart=" + (anyPart == null?null:anyPart.toUsefulString()) +
                ", productGroup=" + (productGroup == null?null: productGroup.toUsefulString())+
                ", passport=" + (passport == null?null: passport.toUsefulString()) +
                ", variant='" + variant + '\'' +
                ", folder=" + (folder == null?null: folder.toUsefulString()) +
                ", note='" + note + '\'' +
                '}';
    }
}
