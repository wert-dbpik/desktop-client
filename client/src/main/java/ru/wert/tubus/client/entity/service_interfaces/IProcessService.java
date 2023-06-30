package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.TechProcess;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IProcessService extends ItemService<TechProcess> {

    TechProcess findByName(String name);

}
