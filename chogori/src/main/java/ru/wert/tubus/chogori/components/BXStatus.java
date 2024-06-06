package ru.wert.tubus.chogori.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import ru.wert.tubus.winform.enums.EDraftStatus;

public class BXStatus {

    private ComboBox<EDraftStatus> bxStatus;


    public void create(ComboBox<EDraftStatus> bxStatus){
        this.bxStatus = bxStatus;

        ObservableList<EDraftStatus> statuses = FXCollections.observableArrayList(EDraftStatus.values());
        bxStatus.setItems(statuses);

        createCellFactory();

        bxStatus.setValue(EDraftStatus.LEGAL);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxStatus.setCellFactory(i -> new ListCell<EDraftStatus>() {
            @Override
            protected void updateItem (EDraftStatus item,boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    Label status = new Label(item.getStatusName());
                    status.setStyle("-fx-font-weight: bold");
                    switch(item.getStatusId()){
                        case 0: status.setStyle("-fx-text-fill: darkgreen; "); //ДЕЙСТВУЕТ
                        case 1: status.setStyle("-fx-text-fill: yellow; "); //ЗАМЕНЕН
                        case 2: status.setStyle("-fx-text-fill: darkred; "); //АННУЛИРОВАН
                    }
                    Label lblStatus = new Label(item.getStatusName());

                    setGraphic(lblStatus);
                }
            }

        });
    }


}
