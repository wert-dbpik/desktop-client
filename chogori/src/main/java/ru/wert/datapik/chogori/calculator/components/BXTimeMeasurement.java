package ru.wert.datapik.chogori.calculator.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;


public class BXTimeMeasurement {

    public static ETimeMeasurement LAST_TIME_MEASUREMENT;
    private ComboBox<ETimeMeasurement> bxTimeMeasurement;

    public void create(ComboBox<ETimeMeasurement> bxTimeMeasurement){
        this.bxTimeMeasurement = bxTimeMeasurement;
        ObservableList<ETimeMeasurement> timeMeasurements = FXCollections.observableArrayList(ETimeMeasurement.values());
        bxTimeMeasurement.setItems(timeMeasurements);

        createCellFactory();
        //Выделяем префикс по умолчанию
        createConverter();

        if(LAST_TIME_MEASUREMENT == null)
            LAST_TIME_MEASUREMENT = ETimeMeasurement.MIN;
        bxTimeMeasurement.setValue(LAST_TIME_MEASUREMENT);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxTimeMeasurement.setCellFactory(i -> new ListCell<ETimeMeasurement>() {
            @Override
            protected void updateItem (ETimeMeasurement item,boolean empty){
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
            public String toString(ETimeMeasurement timeMeasurement) {
                LAST_TIME_MEASUREMENT = timeMeasurement; //последний выбранный префикс становится префиксом по умолчанию
                return timeMeasurement.getTimeName();
            }

            @Override
            public ETimeMeasurement fromString(String string) {
                return null;
            }
        });
    }
}
