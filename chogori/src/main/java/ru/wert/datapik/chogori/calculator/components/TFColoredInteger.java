package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;

public class TFColoredInteger{


    public TFColoredInteger(TextField tf, AbstractOpPlate counter) {
        String style = tf.getStyle();
        String normStyle = counter.getTfNormTime().getStyle();

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) {
                tf.setStyle("-fx-border-color: #FF5555");
                counter.getTfNormTime().setStyle("-fx-border-color: #FF5555");
                return;
            }
            tf.setStyle(style);
            counter.getTfNormTime().setStyle(normStyle);
            counter.countNorm();
        });

        tf.setOnKeyTyped(e->{
            if(tf.isFocused() && !e.getCharacter().matches("[0-9]"))
                e.consume();
        });
    }
}
