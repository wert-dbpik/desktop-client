package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.AnyPartGroup;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IAnyPartGroupService extends ItemService<AnyPartGroup> {

    AnyPartGroup findByName(String name);

}
