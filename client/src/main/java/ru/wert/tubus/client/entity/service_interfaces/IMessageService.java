package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IMessageService extends ItemService<Message> {

    List<Message> findAllByRoom(Room room);
}
