package ru.wert.datapik.utils.common.tableView;

import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.interfaces.CatalogGroup;

public interface CatalogableTable <T extends CatalogGroup> {

    TreeItem<T> getChosenCatalogItem();

    TreeItem<T> getRootItem();


}
