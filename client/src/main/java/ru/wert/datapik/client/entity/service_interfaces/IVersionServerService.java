package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.VersionServer;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IVersionServerService extends ItemService<VersionServer> {

    VersionServer findByName(String name);

}
