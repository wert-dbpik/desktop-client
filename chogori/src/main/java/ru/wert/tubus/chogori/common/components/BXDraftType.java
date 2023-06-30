package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.tubus.winform.enums.EDraftType;

public class BXDraftType {

    private ComboBox<EDraftType> bxType;

    public void create(ComboBox<EDraftType> bxType){
        this.bxType = bxType;

        ObservableList<EDraftType> types = FXCollections.observableArrayList(EDraftType.values());
        bxType.setItems(types);

        createCellFactory();

        createConverter();

        bxType.getSelectionModel().select(0);
    }

    private void createCellFactory() {
        bxType.setCellFactory(i -> new ListCell<EDraftType>() {
            @Override
            protected void updateItem (EDraftType item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getTypeName() + " (" + item.getShortName() + ")");
                }
            }
        });
    }

    private void createConverter() {
        bxType.setConverter(new StringConverter<EDraftType>() {
            @Override
            public String toString(EDraftType eDraftType) {
                return eDraftType.getTypeName();
            }

            @Override
            public EDraftType fromString(String string) {
                return null;
            }
        });
    }
}
