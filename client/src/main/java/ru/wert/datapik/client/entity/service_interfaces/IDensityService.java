package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Density;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IDensityService extends ItemService<Density> {

    Density findByName(String name);

    Density findByValue(double value);

}
