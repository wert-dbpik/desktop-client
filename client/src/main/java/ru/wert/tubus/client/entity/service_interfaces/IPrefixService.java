package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.interfaces.ItemService;

public interface IPrefixService extends ItemService<Prefix> {

    Prefix findByName(String name);

}
