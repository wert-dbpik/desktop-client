package ru.wert.datapik.chogori.calculator.part_calculator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import ru.wert.datapik.chogori.calculator.INormsCounter;

public class PaintingController implements INormsCounter {

    @FXML
    private Label lblNormResult;

    @FXML
    private TextField tfB;

    @FXML
    private ImageView cmbxOnDifficulty;

    @FXML
    private Label lblTimeMeasurement;

    @FXML
    private ImageView ivHelpOnA;

    @FXML
    private ImageView ivHelpOnB;

    @FXML
    private ComboBox<?> cmbxDifficulty;

    @FXML
    private ImageView ivHelpOnHangingTime;

    @FXML
    private TextField tfA;

    @FXML
    private TextField tfHangingTime;

    private PartCalculatorController controller;

    public void init(PartCalculatorController controller){
        this.controller = controller;
    }

    @Override//INormsCounter
    public double countNorm(){
        return 0.0;
    }

    @Override//INormsCounter
    public void clearNorms() {

    }

    @Override//INormsCounter
    public double getNorm() {
        return 0;
    }
}
