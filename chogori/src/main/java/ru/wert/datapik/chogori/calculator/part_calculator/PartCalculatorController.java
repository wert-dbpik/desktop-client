package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import ru.wert.datapik.chogori.calculator.INormsCounter;
import ru.wert.datapik.chogori.common.components.BXDraftType;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.common.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MATERIALS;

public class PartCalculatorController implements INormsCounter {

    @FXML
    private ComboBox<Material> cmbxMaterial;

    @FXML
    private ImageView ivErase;

    @FXML
    private ImageView ivHelpOnPartParameters;

    @FXML
    private TextField tfA;

    @FXML
    private TextField tfB;

    @FXML
    private ImageView ivHelpOnWeight;

    @FXML
    private TextField tfWeight;

    @FXML
    private TextField tfCoat;

    @FXML
    private StackPane spCuttingContainer;

    @FXML
    private CheckBox chboxPainting;

    @FXML
    private CheckBox chbxBending;

    @FXML
    private CheckBox chbxCutting;



    @FXML
    private StackPane spPaintingContainer;

    @FXML
    private ComboBox<ETimeMeasurement> cmbxTimeMeasurement;

    @FXML
    private StackPane spBendingContainer;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;

    @FXML
    void initialize(){

        new BXMaterial().create(cmbxMaterial);
        new BXTimeMeasurement().create(cmbxTimeMeasurement);


//        cmbxTimeMeasurement.setItems(FXCollections.observableArrayList(ETimeMeasurement.allNames()));
        cmbxTimeMeasurement.setValue(ETimeMeasurement.MIN);

    }


    @Override//INormsCounter
    public void clearNorms() {

    }

    @Override//INormsCounter
    public double getNorm() {
        return 0;
    }
}
