package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.MatType;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IMatTypeService extends ItemService<MatType> {

    MatType findByName(String name);

}
