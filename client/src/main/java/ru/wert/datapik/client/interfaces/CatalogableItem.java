package ru.wert.datapik.client.interfaces;


import ru.wert.datapik.client.entity.models.AnyPart;

public interface CatalogableItem extends Item{

    CatalogGroup getCatalogGroup();

    void setCatalogGroup(CatalogGroup catalogGroup);

    AnyPart getAnyPart();

}
