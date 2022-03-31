package ru.wert.datapik.utils.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class BtnDoubleEye{

    private final boolean initState;
    private BtnDouble btnEyeSwitcher;

    public BtnDoubleEye(boolean initState) {
        this.initState = initState;
    }

    public Button create(){

        btnEyeSwitcher = new BtnDouble(
                BTN_EYE_CLOSE_IMG, "Для предпросмотра\nНЕ использовать ALT",
                BTN_EYE_OPEN_IMG, "Для предпросмотра\nиспользовать ALT",
                initState);

        return btnEyeSwitcher;
    }

    public BooleanProperty getStateProperty(){
        return btnEyeSwitcher.getStateProperty();
    }
}
