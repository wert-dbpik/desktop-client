package ru.wert.datapik.chogori.chat;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Message;
import ru.wert.datapik.client.entity.models.Room;

public class ListViewDialog extends ListView<Message> {

    @Getter
    private final Room room;

    public ListViewDialog(Room room) {
        this.room = room;
    }
}
