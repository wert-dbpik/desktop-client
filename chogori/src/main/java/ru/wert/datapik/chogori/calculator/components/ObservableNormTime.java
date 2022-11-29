package ru.wert.datapik.chogori.calculator.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ru.wert.datapik.chogori.calculator.IFormController;

public class ObservableNormTime {

    public ObservableNormTime(ObjectProperty<Double> db, IFormController controller) {
        db.addListener((observable, oldValue, newValue) -> {
            controller.countSumNormTimeByShops();
        });
    }
}
