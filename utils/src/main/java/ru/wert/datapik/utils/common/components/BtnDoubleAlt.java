package ru.wert.datapik.utils.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class BtnDoubleAlt<T extends Item>{

    private final ItemTableView<T> tableView;
    private final boolean initState;
    private BtnDouble btnAltSwitcher;

    public BtnDoubleAlt(ItemTableView<T> tableView, boolean initState) {
        this.tableView = tableView;
        this.initState = initState;
    }

    public Button create(){

        btnAltSwitcher = new BtnDouble(
                BTN_ALT_FALSE_IMG, "Для предпросмотра\nНЕ использовать ALT",
                BTN_ALT_TRUE_IMG, "Для предпросмотра\nиспользовать ALT",
                initState);

        return btnAltSwitcher;
    }

    public BooleanProperty getStateProperty(){
        return btnAltSwitcher.getStateProperty();
    }
}
