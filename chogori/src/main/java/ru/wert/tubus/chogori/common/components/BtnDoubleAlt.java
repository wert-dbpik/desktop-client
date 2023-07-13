package ru.wert.tubus.chogori.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;

import static ru.wert.tubus.chogori.images.BtnImages.*;

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