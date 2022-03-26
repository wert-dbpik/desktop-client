package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;


public interface IAppSettingsService extends ItemService<AppSettings> {

    AppSettings findByName(String name);
    List<AppSettings> findAllByUser(User user);

}
