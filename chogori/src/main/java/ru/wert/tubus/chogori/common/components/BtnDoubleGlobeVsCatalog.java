package ru.wert.tubus.chogori.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.tableView.CatalogableTable;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;

import static ru.wert.tubus.chogori.images.BtnImages.*;

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
            tableView.setGlobalOff(!btnGlobalOrCatalog.getStateProperty().get());
            if(tableView.isGlobalOff())
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
