package ru.wert.tubus.chogori.common.components;

import javafx.scene.control.Button;
import ru.wert.tubus.chogori.statics.AppStatic;

public class BtnCancel {

    public void create(Button btnCancel){
        btnCancel.setOnAction(AppStatic::closeWindow);
    }
}
