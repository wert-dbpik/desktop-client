package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;

public class TFColoredInteger{


    public TFColoredInteger(TextField tf, AbstractNormsCounter counter) {
        String style = tf.getStyle();

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) {
                tf.setStyle("-fx-border-color: #FF5555");
                return;
            }
            tf.setStyle(style);
            counter.setNormTime();
        });

        tf.setOnKeyTyped(e->{
            if(tf.isFocused() && !e.getCharacter().matches("[0-9]"))
                e.consume();
        });
    }
}
