package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Room;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IChatGroupService extends ItemService<Room> {

    Room findByName(String name);

}
