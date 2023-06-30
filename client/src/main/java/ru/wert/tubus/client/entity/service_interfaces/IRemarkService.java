package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Remark;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IRemarkService extends ItemService<Remark> {

    Remark findByName(String name);

    List<Remark> findAllByPassport(Passport passport);

    List<Pic> getPics(Remark remark);

}
