package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IUserGroupService extends ItemService<UserGroup> {

    UserGroup findByName(String name);

}
