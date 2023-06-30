package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.MatType;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IMatTypeService extends ItemService<MatType> {

    MatType findByName(String name);

}
