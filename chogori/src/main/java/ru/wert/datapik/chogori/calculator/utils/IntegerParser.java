package ru.wert.datapik.chogori.calculator.utils;

import javafx.scene.control.TextField;

public class IntegerParser {

    public static int getValue(TextField tf){
        int value = 0;
        try {
            value = Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
        return value;
    }
}
