package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.VersionServer;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IVersionServerService extends ItemService<VersionServer> {

    VersionServer findByName(String name);

}
