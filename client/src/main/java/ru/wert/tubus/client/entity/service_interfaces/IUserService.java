package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IUserService extends ItemService<User> {

    User findByName(String name);
    User findByPassword(String pass);
    List<Room> subscribeRoom(User user, Room room);
    List<Room> unsubscribeRoom(User user, Room room);

}
