package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.VersionDesktop;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IVersionDesktopService extends ItemService<VersionDesktop> {

    VersionDesktop findByName(String name);

}
