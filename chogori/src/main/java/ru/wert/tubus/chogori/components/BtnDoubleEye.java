package ru.wert.tubus.chogori.components;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Button;
import ru.wert.tubus.chogori.images.BtnImages;

public class BtnDoubleEye{

    private final boolean initState;
    private BtnDouble btnEyeSwitcher;

    public BtnDoubleEye(boolean initState) {
        this.initState = initState;
    }

    public Button create(){

        btnEyeSwitcher = new BtnDouble(
                BtnImages.BTN_EYE_CLOSE_IMG, "Для предпросмотра\nНЕ использовать ALT",
                BtnImages.BTN_EYE_OPEN_IMG, "Для предпросмотра\nиспользовать ALT",
                initState);

        return btnEyeSwitcher;
    }

    public BooleanProperty getStateProperty(){
        return btnEyeSwitcher.getStateProperty();
    }
}
