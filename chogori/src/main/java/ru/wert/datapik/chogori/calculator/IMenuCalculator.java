package ru.wert.datapik.chogori.calculator;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import java.util.List;

public interface IMenuCalculator {

    ObservableList<AbstractNormsCounter> getAddedOperations();

    ListView<VBox> getListViewTechOperations();

    void countSumNormTimeByShops();

    ComboBox<ETimeMeasurement> getCmbxTimeMeasurement();

    ComboBox<Material> getCmbxMaterial();

    TextField getTfA();

    TextField getTfB();
}
