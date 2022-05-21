package ru.wert.datapik.client.entity.service_interfaces;

import javafx.collections.ObservableSet;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IAnyPartService extends ItemService<AnyPart> {

    AnyPart findByName(String name);
}
