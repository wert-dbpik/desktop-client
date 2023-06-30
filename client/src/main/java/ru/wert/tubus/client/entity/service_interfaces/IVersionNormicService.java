package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.VersionNormic;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IVersionNormicService extends ItemService<VersionNormic> {

    VersionNormic findByName(String name);

}
