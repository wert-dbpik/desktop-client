package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.interfaces.IFormController;

public class TFNormTime {


    public TFNormTime(TextField tf, IFormController controller) {

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.countSumNormTimeByShops();
        });

    }
}
