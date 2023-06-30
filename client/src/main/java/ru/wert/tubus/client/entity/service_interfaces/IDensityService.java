package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Density;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IDensityService extends ItemService<Density> {

    Density findByName(String name);

    Density findByValue(double value);

}
