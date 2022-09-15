package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IRemarkService extends ItemService<Remark> {

    Remark findByName(String name);

}
