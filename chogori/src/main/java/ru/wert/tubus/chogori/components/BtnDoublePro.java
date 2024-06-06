package ru.wert.tubus.chogori.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.tubus.chogori.images.BtnImages;

public class BtnDoublePro {

    private final boolean initState;
    private BtnDouble btnProSwitcher;

    public BtnDoublePro(boolean initState) {
        this.initState = initState;
    }

    public Button create(){

        btnProSwitcher = new BtnDouble(
                BtnImages.BTN_PRO_OFF_IMG, "PRO поиск выключен",
                BtnImages.BTN_PRO_ON_IMG, "Работет PRO поиск",
                initState);

        return btnProSwitcher;
    }

    public BooleanProperty getStateProperty(){
        return btnProSwitcher.getStateProperty();
    }
}
