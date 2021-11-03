package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.AsmItem;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IAsmItemService extends ItemService<AsmItem> {

    AsmItem findByName(String name);

}
