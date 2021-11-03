package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Coat;
import ru.wert.datapik.client.interfaces.ItemService;


public interface ICoatService extends ItemService<Coat> {

    Coat findByName(String name);

}
