package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class BXPage {

    public void create(ComboBox<Integer> bxPage){
        ObservableList<Integer> pages = FXCollections.observableArrayList();
        for(int i = 1; i < 100; i++){
            pages.add(i);
        }
        bxPage.setItems(pages);
        bxPage.getSelectionModel().select(0);
    }
}
