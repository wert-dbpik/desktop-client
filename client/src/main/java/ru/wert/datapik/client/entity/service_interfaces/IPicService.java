package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IPicService extends ItemService<Pic> {

    Pic findByName(String name);

}
