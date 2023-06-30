package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IRoomService extends ItemService<Room> {

    Room findByName(String name);
    Room addRoommates(List<String> userIds, Room room);
    Room removeRoommates(List<String> userIds, Room room);

}
