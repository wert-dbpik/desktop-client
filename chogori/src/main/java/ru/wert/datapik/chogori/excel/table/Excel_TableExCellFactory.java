package ru.wert.datapik.chogori.excel.table;


import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class Excel_TableExCellFactory extends Excel_TableCellFactory {

    public Excel_TableExCellFactory(Excel_TableView editorTable, final String exId) {
        super(editorTable);
        //Операция отслеживает посимвольный ввод цифр
        UnaryOperator<TextFormatter.Change> numbersOnlyFilter = change -> {
            String s = change.getText();
            if(s.matches("^([0-9]+)?$")){
                return change;
            }
            return null;
        };

        TextFormatter<String> tf = new TextFormatter<String>(
                new StringInBracketsConverter(), "", numbersOnlyFilter);
        textField.setTextFormatter(tf);

        textField.setOnKeyReleased(event -> {
                textField.end();
                textField.positionCaret(textField.getText().length()-1);
                event.consume();
        });

//        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if(!textField.getText().matches("^([0-9()]+)?$"))
//                textField.setText("");
//        });

    }


}
