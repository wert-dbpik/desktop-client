package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;

public class ChBox {


    public ChBox(CheckBox chbx, AbstractOpPlate counter) {

        chbx.selectedProperty().addListener((observable, oldValue, newValue) -> {
            counter.setNormTime();
        });

    }
}
