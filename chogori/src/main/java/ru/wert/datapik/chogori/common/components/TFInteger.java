package ru.wert.datapik.chogori.common.components;

import javafx.scene.control.TextField;

public class TFInteger extends TextField {

    TextField tf;

    public TFInteger(TextField tf) {
        this.tf = tf;
    }

    public Integer getIntegerValue(){
        String text = tf.getText();
        if(text == null || text.equals("")) text = "0";
        int val = 0;
        try {
            val = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
        }
        return val;
    }
}
