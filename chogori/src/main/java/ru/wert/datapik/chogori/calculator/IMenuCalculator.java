package ru.wert.datapik.chogori.calculator;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

public interface IMenuCalculator {

    ObservableList<AbstractOpPlate> getAddedOperations();

    ListView<VBox> getListViewTechOperations();

    void countSumNormTimeByShops();

    ComboBox<ETimeMeasurement> getCmbxTimeMeasurement();

//    ComboBox<Material> getCmbxMaterial();
//
//    TextField getTfA();
//
//    TextField getTfB();
}
