package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;


public interface IPassportService extends ItemService<Passport> {

    Passport findByPrefixIdAndNumber(Prefix prefix, String number);

    List<Passport> findAllByName(String name);

}
