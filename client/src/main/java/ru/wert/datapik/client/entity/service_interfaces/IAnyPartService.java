package ru.wert.datapik.client.entity.service_interfaces;

import javafx.collections.ObservableSet;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IAnyPartService extends ItemService<AnyPart> {

    AnyPart findByName(String name);
    
    //ЧЕРТЕЖИ

//    ObservableSet<Draft> findDrafts(AnyPart part);
//
//    ObservableSet<Draft> addDrafts(AnyPart part, Draft draft);
//
//    ObservableSet<Draft> removeDrafts(AnyPart part, Draft draft);
}
