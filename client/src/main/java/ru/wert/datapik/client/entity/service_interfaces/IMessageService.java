package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Message;
import ru.wert.datapik.client.entity.models.Room;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;


public interface IMessageService extends ItemService<Message> {

    List<Message> findAllByRoom(Room room);
}
