package ru.wert.tubus.chogori.common.tableView;

import javafx.scene.control.TreeItem;
import ru.wert.tubus.client.interfaces.CatalogGroup;

public interface CatalogableTable <T extends CatalogGroup>{

    TreeItem<T> getChosenCatalogItem();

    TreeItem<T> getRootItem();

    TreeItem<T> getUpwardRow();

    void setUpwardRow(TreeItem<T> item);

    /**
     * Обновляет таблицу независимо от выделения в TreeView
     */
    void updateVisibleLeafOfTableView(CatalogGroup selectedCatalogGroup);


}
