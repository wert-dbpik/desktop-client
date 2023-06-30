package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IPicService extends ItemService<Pic> {

    Pic findByName(String name);

}
