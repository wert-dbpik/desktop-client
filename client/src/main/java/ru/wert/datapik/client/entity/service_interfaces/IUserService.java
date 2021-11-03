package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IUserService extends ItemService<User> {

    User findByName(String name);
    User findByPassword(String pass);

}
