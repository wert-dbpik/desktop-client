package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.AnyPartGroup;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IAnyPartGroupService extends ItemService<AnyPartGroup> {

    AnyPartGroup findByName(String name);

}
