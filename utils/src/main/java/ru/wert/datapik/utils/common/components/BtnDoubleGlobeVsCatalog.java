package ru.wert.datapik.utils.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class BtnDoubleGlobeVsCatalog{

    private ItemTableView<Item> tableView;
    private final boolean initState;
    private BtnDouble btnGlobalOrCatalog;

    public BtnDoubleGlobeVsCatalog(ItemTableView<Item> tableView, boolean initState) {
        this.tableView = tableView;
        this.initState = initState;
    }

    public Button create(){

        btnGlobalOrCatalog = new BtnDouble(
                BTN_CATALOG_IMG, "Каталог",
                BTN_GLOBE_IMG, "Все комплекты чертежей",

                initState);

        btnGlobalOrCatalog.getStateProperty().addListener((observable) -> {
            //Меняем значение переменной globalOn на противоположное и апдейтим таблицу
            tableView.setGlobalOn(!btnGlobalOrCatalog.getStateProperty().get());
            if(tableView.isGlobalOn())
                tableView.updateTableView();
            else
            ((CatalogableTable) tableView).updateVisibleLeafOfTableView(
                    (CatalogGroup) ((CatalogableTable) tableView).getUpwardRow().getValue());
        });

        return btnGlobalOrCatalog;
    }

    public BooleanProperty getStateProperty(){
        return btnGlobalOrCatalog.getStateProperty();
    }

}
