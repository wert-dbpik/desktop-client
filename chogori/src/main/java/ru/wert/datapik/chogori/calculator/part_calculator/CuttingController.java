package ru.wert.datapik.chogori.calculator.part_calculator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import ru.wert.datapik.chogori.calculator.INormsCounter;

public class CuttingController  implements INormsCounter {

    @FXML
    private Label lblNormResult;

    @FXML
    private TextField tfNumOfHoles;

    @FXML
    private ImageView ivHelpOnNumOfHoles;

    @FXML
    private TextField tfNumOfPerfHoles;

    @FXML
    private CheckBox chbxUseStripping;

    @FXML
    private Label lblTimeMeasurement;

    @FXML
    private ImageView ivHelpOnExtraPerimeter;

    @FXML
    private TextField tfExtraPerimeter;

    @FXML
    private ImageView ivHelpOnUseStripping;

    @FXML
    private ImageView ivHelpOnNumOfPerfHoles;


    @Override//INormsCounter
    public void clearNorms() {

    }

    @Override//INormsCounter
    public double getNorm() {
        return 0;
    }
}
