package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.INormsCounter;

public class BendingController implements INormsCounter {

    @FXML
    private Label lblNormResult;

    @FXML
    private TextField tfNumOfMen;

    @FXML
    private Label lblTimeMeasurement;

    @FXML
    private TextField tfNumOfBendings;

    @FXML
    private ComboBox<?> cmbxEquipment;


    @Override//INormsCounter
    public void clearNorms() {

    }

    @Override//INormsCounter
    public double getNorm() {
        return 0;
    }
}
