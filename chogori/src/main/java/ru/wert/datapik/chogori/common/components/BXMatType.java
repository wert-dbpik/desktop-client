package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.client.entity.models.MatType;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MAT_TYPES;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_DEFAULT_MAT_TYPE;

public class BXMatType {

    private static MatType LAST_MAT_TYPE;
    private ComboBox<MatType> bxMatType;


    public ComboBox<MatType> create(ComboBox<MatType> bxMatType){
        this.bxMatType = bxMatType;


        ObservableList<MatType> matTypes = FXCollections.observableArrayList(CH_MAT_TYPES.findAll());
        bxMatType.setItems(matTypes);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_MAT_TYPE == null)
            LAST_MAT_TYPE = CH_MAT_TYPES.findByName(CH_DEFAULT_MAT_TYPE);
        bxMatType.setValue(LAST_MAT_TYPE);

        return bxMatType;

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxMatType.setCellFactory(i -> new ListCell<MatType>() {
            @Override
            protected void updateItem (MatType item,boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }

        });
    }

    private void createConverter() {
        bxMatType.setConverter(new StringConverter<MatType>() {
            @Override
            public String toString(MatType MatType) {
                LAST_MAT_TYPE = MatType; //последний выбранный префикс становится префиксом по умолчанию
                return MatType.getName();
            }

            @Override
            public MatType fromString(String string) {
                return null;
            }
        });
    }
}
