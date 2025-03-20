package ru.wert.tubus.chogori.chat.cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DateSeparatorController {

    @FXML
    private Label lblDate;

    public void init(String date){
        lblDate.setText(date);
    }
}
