package ru.wert.tubus.chogori.lists;

import javafx.scene.control.ListCell;
import ru.wert.tubus.client.interfaces.Item;

public class SimpleListCell<T extends Item> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
                setText(item.toUsefulString());
//            setGraphic(new ImageView(TREE_NODE_IMG));

        }
    }
}
