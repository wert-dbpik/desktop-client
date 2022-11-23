package ru.wert.datapik.chogori.calculator.utils;

import javafx.scene.control.TextField;

public class DoubleParser {

    public static double getValue(TextField tf){
        double value = 0;
        try {
            value = Double.parseDouble(tf.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return value;
    }
}
