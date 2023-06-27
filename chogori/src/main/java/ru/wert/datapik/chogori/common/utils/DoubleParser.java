package ru.wert.datapik.chogori.common.utils;

import javafx.scene.control.TextField;

public class DoubleParser {

    public static double getValue(TextField tf){
        double value = 0;
        try {
            String str = tf.getText().trim().replace(",", ".");
            value = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return value;
    }
}
