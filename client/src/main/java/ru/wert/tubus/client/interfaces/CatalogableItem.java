package ru.wert.tubus.client.interfaces;


import ru.wert.tubus.client.entity.models.AnyPart;

public interface CatalogableItem extends Item{

    CatalogGroup getCatalogGroup();

    void setCatalogGroup(CatalogGroup catalogGroup);

    AnyPart getAnyPart();

}
