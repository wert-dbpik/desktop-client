package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.VersionNormic;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IVersionNormicService extends ItemService<VersionNormic> {

    VersionNormic findByName(String name);

}
