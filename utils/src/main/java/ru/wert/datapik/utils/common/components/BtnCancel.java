package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import ru.wert.datapik.utils.statics.AppStatic;

public class BtnCancel {

    public void create(Button btnCancel){
        btnCancel.setOnAction(AppStatic::closeWindow);
    }
}
