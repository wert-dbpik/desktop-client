package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.AsmItem;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IAsmItemService extends ItemService<AsmItem> {

    AsmItem findByName(String name);

}
