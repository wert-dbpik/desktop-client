package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.AppSettings;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IAppSettingsService extends ItemService<AppSettings> {

    AppSettings findByName(String name);
    List<AppSettings> findAllByUser(User user);

}
