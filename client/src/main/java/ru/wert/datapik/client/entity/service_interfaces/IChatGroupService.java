package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.ChatGroup;
import ru.wert.datapik.client.entity.models.Coat;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IChatGroupService extends ItemService<ChatGroup> {

    ChatGroup findByName(String name);

}
