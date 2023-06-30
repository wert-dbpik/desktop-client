package ru.wert.tubus.chogori.orthography;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public class Orthography {

    private static char[] allowedDigits = "0123456789.,".toCharArray();

    //Десятичный разделитель. В Postgresql по умолчанию используется точка
    private static char decimalSeparator = '.';


    /**
     * ТОЛЬКО ЦИФРЫ при наборе
     * @param e
     */
    public static void typeOnlyDigits(KeyEvent e){
        TextInputControl textInputControl = (TextInputControl) e.getSource();
        char s = e.getCharacter().charAt(0);

        if(s == ',' || s == '.') {
            e.consume();
            String text = textInputControl.getText();
            textInputControl.setText(text.concat(String.valueOf(decimalSeparator)));
            textInputControl.positionCaret(text.length() + 1);
            return;
        }
        boolean flag = false;
        for(char c: allowedDigits) {
            if (s == c) {
                flag = true;
                break;
            }
        }
        if(!flag) e.consume();
    }

    /**
     * Проверка введенного числа на корректность
     * @param text
     * @return
     */
    public static String onlyDigits(String text){
       text = text.trim();
       char[] textArray = text.toCharArray();

        for(int i = 0; i < text.length(); i++){
            if((textArray[i] == ',') || (textArray[i] == '.'))
                textArray[i] = decimalSeparator;
            boolean flag = false;
            for(char c : allowedDigits)
                if(textArray[i] == c) {
                    flag = true;
                    break;
                }
            if(!flag) return null;
        }

        return String.valueOf(textArray);
    }

//    public ChangeListener createRuFirstListener(){
//
//        ChangeListener<String> ch = (observable, oldValue, newValue) -> {
//            if(newValue.)
//        });
//
//        return ch;
//    }


}
