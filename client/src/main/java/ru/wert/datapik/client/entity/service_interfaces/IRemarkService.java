package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;


public interface IRemarkService extends ItemService<Remark> {

    Remark findByName(String name);

    List<Remark> findAllByPassport(Passport passport);

}
