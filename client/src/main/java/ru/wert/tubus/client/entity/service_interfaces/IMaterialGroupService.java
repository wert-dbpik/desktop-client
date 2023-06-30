package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IMaterialGroupService extends ItemService<MaterialGroup> {

    MaterialGroup findByName(String name);

    MaterialGroup getRootItem();
}
