package ru.wert.datapik.utils.views.lists;

import javafx.scene.control.ListCell;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.utils.BLConst;

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
