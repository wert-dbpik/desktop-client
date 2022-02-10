package ru.wert.datapik.utils.common.commands;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

public class Catalogs<P extends Item, T extends CatalogGroup> {

    /**
     * Обновление форм после добавления или изменения группы
     * @param item ProductGroup
     */
    public static <T extends CatalogGroup> void updateFormsWhenAddedOrChanged(Item_TreeView<Item, T> treeView, ItemTableView<Item> catTableView, ProductGroup item) {
        TreeItem<T> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();
        //Заранее раскрываем выбранный узел
        //selectedTreeItemInTree != null - условие при добавлении в корень
        if(selectedTreeItemInTree != null) selectedTreeItemInTree.setExpanded(true);

        Platform.runLater(() -> {
            treeView.updateView();
            TreeItem<? extends CatalogGroup> addedTreeItem = treeView.findTreeItemById(item.getId());
            treeView.getFocusModel().focus(selectedItemIndex + addedTreeItem.getParent().getChildren().indexOf(addedTreeItem) + 1);
            if (catTableView != null) { //Если имеем дело с каталогом, а не только с деревом
                ((CatalogableTable<T>)catTableView).updateOnlyTableView(((CatalogableTable<T>)catTableView).getSelectedTreeItem().getValue());
                catTableView.getSelectionModel().select(item);
                catTableView.scrollTo(item);
            }
        });
    }

}
