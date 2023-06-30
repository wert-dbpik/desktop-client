package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IAnyPartService extends ItemService<AnyPart> {

    AnyPart findByName(String name);
}
