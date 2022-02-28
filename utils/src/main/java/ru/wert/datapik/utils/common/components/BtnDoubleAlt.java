package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class BtnDoubleAlt<T extends Item>{

    private final ItemTableView<T> draftTableView;

    public BtnDoubleAlt(ItemTableView<T> draftTableView) {
        this.draftTableView = draftTableView;
    }

    public Button create(){

        BtnDouble btnAltSwitcher = new BtnDouble(
                BTN_ALT_TRUE_IMG, "Использовать ALT",
                BTN_ALT_FALSE_IMG, "НЕ использовать ALT");
        btnAltSwitcher.setOnAction(e->{
            draftTableView.setAltOn(btnAltSwitcher.getLogicProperty());
        });
        return btnAltSwitcher;
    }
}
