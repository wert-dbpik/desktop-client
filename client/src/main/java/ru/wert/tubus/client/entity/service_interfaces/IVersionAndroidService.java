package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.VersionAndroid;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IVersionAndroidService extends ItemService<VersionAndroid> {

    VersionAndroid findByName(String name);

}
