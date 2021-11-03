package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IUserGroupService extends ItemService<UserGroup> {

    UserGroup findByName(String name);

}
