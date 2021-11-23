package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.VersionAndroid;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IVersionAndroidService extends ItemService<VersionAndroid> {

    VersionAndroid findByName(String name);

}
