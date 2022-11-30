package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;

public class CmBx {


    public CmBx(ComboBox<?> cmbx, AbstractOpPlate counter) {

        cmbx.valueProperty().addListener((observable, oldValue, newValue) -> {
            counter.countNorm();
        });

    }
}
