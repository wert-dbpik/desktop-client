package ru.wert.tubus.chogori.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class BXPage {

    public void create(ComboBox<Integer> bxPage){
        ObservableList<Integer> pages = FXCollections.observableArrayList();
        for(int i = 0; i < 100; i++){
            pages.add(i);
        }
        bxPage.setItems(pages);
        bxPage.setStyle("-fx-font-family: Arial; -fx-font-size: 12;");
//        bxPage.getSelectionModel().select(0);
    }
}
