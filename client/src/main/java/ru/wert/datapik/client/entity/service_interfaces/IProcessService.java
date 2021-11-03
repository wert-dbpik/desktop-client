package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.TechProcess;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IProcessService extends ItemService<TechProcess> {

    TechProcess findByName(String name);

}
