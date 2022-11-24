package ru.wert.datapik.chogori.calculator.components;

import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.AbstractOperationCounter;

/**
 * Класс перехватывает ошибочно введенные значения,
 * а так же ограничивает введение чисел только цифрами от 0 до 9 и знаками , и .
 * запятую меняет на точку
 */
public class TFColoredDouble {

    public TFColoredDouble(TextField tf, AbstractOperationCounter counter) {
        String style = tf.getStyle();

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) {
                tf.setStyle("-fx-border-color: #FF5555");
                return;
            }

            try {
                Double.parseDouble(tf.getText().trim());
                tf.setStyle(style);
                counter.setNormTime();
            } catch (Exception e) {
                tf.setStyle("-fx-border-color: #FF5555");
//                return;
            }
        });

        tf.setOnKeyTyped(e->{
            if(tf.isFocused() && !e.getCharacter().matches("[0-9.,]"))
                e.consume();
            else if(tf.isFocused() && e.getCharacter().matches("[,]")) {
                int pos = tf.getCaretPosition();
                tf.setText(tf.getText().concat("."));
                tf.positionCaret(pos+1);
                e.consume();
            }
        });
    }
}
