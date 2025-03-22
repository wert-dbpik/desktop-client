package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IRoomService extends ItemService<Room> {

    Room findByName(String name);
    Room addRoommates(List<String> userIds, Room room);
    Room removeRoommates(List<String> userIds, Room room);
    Room setUserVisibility(Long roomId, Long userId, boolean isVisible);
    Room setUserMembership(Long roomId, Long userId, boolean isMember);

}
