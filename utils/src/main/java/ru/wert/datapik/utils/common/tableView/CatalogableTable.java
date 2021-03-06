package ru.wert.datapik.utils.common.tableView;

import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.utils.common.interfaces.IFormView;

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
