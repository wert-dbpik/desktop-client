package ru.wert.datapik.chogori.chat.cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class CardController {

    @FXML
    private Label lblCardName;

    @FXML
    private VBox vbImageView;

    @FXML
    private AnchorPane chatCard;

    public void init(String cardName, ImageView imageView){
        lblCardName.setText(cardName);
        vbImageView.getChildren().add(imageView);
    }
}
