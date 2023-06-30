package ru.wert.tubus.chogori.common.commands;

import javafx.event.Event;
import ru.wert.tubus.client.interfaces.Item;

import java.util.List;

public interface ItemCommands<P extends Item> {

    void add(Event event, P newItem);
    void copy(Event event);
    void change(Event event, P item);
    void delete(Event event, List<P> items);
}
