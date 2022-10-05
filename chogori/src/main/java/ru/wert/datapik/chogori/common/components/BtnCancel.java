package ru.wert.datapik.chogori.common.components;

import javafx.scene.control.Button;
import ru.wert.datapik.chogori.statics.AppStatic;

public class BtnCancel {

    public void create(Button btnCancel){
        btnCancel.setOnAction(AppStatic::closeWindow);
    }
}
