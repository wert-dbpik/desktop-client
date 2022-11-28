package ru.wert.datapik.chogori.calculator;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

public interface IFormController {

    ObservableList<AbstractOpPlate> getAddedPlates();

    ListView<VBox> getListViewTechOperations();

    void countSumNormTimeByShops();

    ComboBox<ETimeMeasurement> getCmbxTimeMeasurement();

    void setCurrentMechTime(double time);
    void setCurrentPaintTime(double time);
    void setCurrentAssmTime(double time);
    void setCurrentPackTime(double time);


}
