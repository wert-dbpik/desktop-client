package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.chogori.calculator.INormsCounter;

public class PartCalculatorController implements INormsCounter {


    @FXML
    private StackPane spCuttingContainer;

    @FXML
    private CheckBox chboxPainting;

    @FXML
    private TextField tfB;

    @FXML
    private StackPane spPaintingContainer;

    @FXML
    private ComboBox<?> cmbxTimeMeasurement;

    @FXML
    private ComboBox<?> cmbxMaterial;

    @FXML
    private CheckBox chbxBending;

    @FXML
    private Label lblWeight;

    @FXML
    private StackPane spBendingContainer;

    @FXML
    private TextField tfThickness;

    @FXML
    private CheckBox chbxCutting;

    @FXML
    private TextField tfA;

    @FXML
    private ImageView ivHelpOnPartParameters;

    @FXML
    private ImageView ivHelpOnWeight;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;


    @Override//INormsCounter
    public void clearNorms() {

    }

    @Override//INormsCounter
    public double getNorm() {
        return 0;
    }
}
