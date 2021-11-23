package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IVersionDesktopService extends ItemService<VersionDesktop> {

    VersionDesktop findByName(String name);

}
