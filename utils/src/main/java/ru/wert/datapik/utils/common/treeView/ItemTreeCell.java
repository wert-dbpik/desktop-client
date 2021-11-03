package ru.wert.datapik.utils.common.treeView;

import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import ru.wert.datapik.client.interfaces.CatalogGroup;

import static ru.wert.datapik.utils.images.AppImages.*;

public class ItemTreeCell<T extends CatalogGroup> extends TreeCell<T> {

    @SuppressWarnings("unchecked")
    public ItemTreeCell() {

        setOnMouseClicked((e)->{
            TreeCell<T> cell = (TreeCell<T>) e.getSource();
            if(cell.isEmpty())
                cell.getTreeView().getSelectionModel().clearSelection();
        });
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toUsefulString());
            setGraphic(new ImageView(TREE_NODE_IMG));

        }
    }

    @Override
    public Dragboard startDragAndDrop(TransferMode... transferModes) {
        return super.startDragAndDrop(transferModes);

    }
}
