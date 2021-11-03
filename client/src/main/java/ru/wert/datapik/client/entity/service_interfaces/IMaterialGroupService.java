package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IMaterialGroupService extends ItemService<MaterialGroup> {

    MaterialGroup findByName(String name);

    MaterialGroup getRootItem();
}
