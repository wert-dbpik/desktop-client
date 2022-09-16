package ru.wert.datapik.utils.common.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

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
