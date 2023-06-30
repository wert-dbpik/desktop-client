package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IPassportService extends ItemService<Passport> {

    Passport findByPrefixIdAndNumber(Prefix prefix, String number);

    List<Passport> findAllByName(String name);

}
