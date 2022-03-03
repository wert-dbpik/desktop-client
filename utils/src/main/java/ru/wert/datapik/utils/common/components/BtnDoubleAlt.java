package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class BtnDoubleAlt<T extends Item>{

    private final ItemTableView<T> tableView;
    private final boolean initState;

    public BtnDoubleAlt(ItemTableView<T> tableView, boolean initState) {
        this.tableView = tableView;
        this.initState = initState;
    }

    public Button create(){

        BtnDouble btnAltSwitcher = new BtnDouble(
                BTN_ALT_FALSE_IMG, "Для предпросмотра\nНЕ использовать ALT",
                BTN_ALT_TRUE_IMG, "Для предпросмотра\nиспользовать ALT",
                initState);

        btnAltSwitcher.setOnAction(e->{
            tableView.setAltOn(btnAltSwitcher.getLogicProperty());
        });
        return btnAltSwitcher;
    }
}
