package ru.wert.datapik.chogori.common.components;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class TextAreaBlobbing{

    private Text textHolder = new Text();
    private double oldHeight = 0;

    public TextAreaBlobbing(TextArea ta) {
        ta.setId("blobTextArea");

        textHolder.textProperty().bind(ta.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                ta.setPrefHeight(newHeight);
                ta.setMinHeight(newHeight);
            }
        });
    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//
//        textHolder.textProperty().bind(textProperty());
//        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldHeight != newValue.getHeight()) {
//                oldHeight = newValue.getHeight();
//                double newHeight = textHolder.getLayoutBounds().getHeight() + 20;
//                setPrefHeight(newHeight);
//                setMinHeight(newHeight);
//            }
//        });
//    }
}
