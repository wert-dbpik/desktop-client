package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Coat;
import ru.wert.tubus.client.interfaces.ItemService;


public interface ICoatService extends ItemService<Coat> {

    Coat findByName(String name);

}
