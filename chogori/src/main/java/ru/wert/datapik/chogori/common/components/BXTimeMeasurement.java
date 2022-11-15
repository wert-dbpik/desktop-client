package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.part_calculator.ETimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_MATERIALS;


public class BXTimeMeasurement {

    private static ETimeMeasurement LAST_MEASUREMENT;
    private ComboBox<ETimeMeasurement> bxTimeMeasurement;

    public void create(ComboBox<ETimeMeasurement> bxTimeMeasurement){
        this.bxTimeMeasurement = bxTimeMeasurement;
        ObservableList<ETimeMeasurement> materials = FXCollections.observableArrayList(ETimeMeasurement.allValues());


        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        bxTimeMeasurement.setItems(materials);

        if(LAST_MEASUREMENT == null)
            LAST_MEASUREMENT = ETimeMeasurement.MIN;
        bxTimeMeasurement.setValue(LAST_MEASUREMENT);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxTimeMeasurement.setCellFactory(i -> new ListCell<ETimeMeasurement>() {
            @Override
            protected void updateItem (ETimeMeasurement item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getTimeName());
                }
            }

        });
    }

    private void createConverter() {
        bxTimeMeasurement.setConverter(new StringConverter<ETimeMeasurement>() {
            @Override
            public String toString(ETimeMeasurement measurement) {
                LAST_MEASUREMENT = measurement; //последний выбранный префикс становится префиксом по умолчанию
                return measurement.getTimeName();
            }

            @Override
            public ETimeMeasurement fromString(String string) {
                return null;
            }
        });
    }
}
