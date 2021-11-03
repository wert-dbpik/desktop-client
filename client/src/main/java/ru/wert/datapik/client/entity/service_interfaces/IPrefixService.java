package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.interfaces.ItemService;

public interface IPrefixService extends ItemService<Prefix> {

    Prefix findByName(String name);

}
