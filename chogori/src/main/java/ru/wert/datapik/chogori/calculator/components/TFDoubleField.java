package ru.wert.datapik.chogori.calculator.components;


import javafx.scene.control.TextField;

public class TFDoubleField{

    public TFDoubleField(TextField tf) {

        tf.setOnKeyTyped(e->{
            if(tf.isFocused() && !e.getCharacter().matches("[0-9.]"))
                e.consume();
        });
    }
}
