package ru.wert.tubus.chogori.chat.cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class dateSeparatorController {

    @FXML
    private Label lblDate;

    @FXML
    private VBox vbImageView;

    @FXML
    private AnchorPane chatCard;

    public void init(String date){
        lblDate.setText(date);
    }
}
