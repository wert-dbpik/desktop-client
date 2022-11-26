package ru.wert.datapik.chogori.calculator;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.entities.OpCutting;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpDetail;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.calculator.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import static ru.wert.datapik.chogori.calculator.AbstractOpPlate.*;
import static ru.wert.datapik.chogori.calculator.enums.EOpType.CUTTING;

public class FormPartController implements IFormMenu{

    @FXML @Getter
    private TextField tfPartName;

    @FXML @Getter
    private ComboBox<Material> cmbxMaterial;

    @FXML @Getter
    private ComboBox<ETimeMeasurement> cmbxTimeMeasurement;

    @FXML @Getter
    private ListView<VBox> listViewTechOperations;

    @FXML
    private ImageView ivAddOperation;

    @FXML
    private ImageView ivErase;

    @FXML
    private ImageView ivHelpOnPartParameters;

    @FXML @Getter
    private TextField tfA;

    @FXML @Getter
    private TextField tfB;

    @FXML
    private ImageView ivHelpOnWeight;

    @FXML
    private TextField tfWeight;

    @FXML
    private TextField tfCoat;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;

    @FXML @Getter
    private TextField tfMechanicalTime;

    @FXML @Getter
    private TextField tfPaintingTime;

    @FXML
    private Label lblTimeMeasure;

    @FXML @Getter
    private TextField tfTotalTime;

    private MenuCalculator menu;

    private double ro; //Плотность
    private double t; //Толщина
    private double paramA; //параметр А
    private double paramB; //параметр B

    @Getter private ObservableList<AbstractOpPlate> addedOperations;


    public void init(TextField tfName, OpDetail opData) {
        tfPartName.setText(tfName.getText());
        tfName.textProperty().bindBidirectional(tfPartName.textProperty());
        deployData(opData);
    }

    private void deployData(OpData opData) {
        for(IOpPlate plate : ((OpDetail)opData).getOperations()){
            switch(plate.getOpData().getOpType()){
                case CUTTING: menu.addCattingOperation((OpCutting) opData);
            }
        }

        listViewTechOperations.setItems(opData.getOperations());
    }


    @FXML
    void initialize(){

        addedOperations = FXCollections.observableArrayList();

        new BXMaterial().create(cmbxMaterial);
        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        menu = new MenuCalculator(this, addedOperations, listViewTechOperations);
        menu.getItems().addAll(menu.getAddCutting(), menu.getAddBending(), menu.getAddLocksmith());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddWeldLongSeam(), menu.getAddWeldingDotted());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddPainting());

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });

        cmbxTimeMeasurement.valueProperty().addListener((observable, oldValue, newValue) -> {
            for(AbstractOpPlate nc : addedOperations){
                nc.setTimeMeasurement(newValue);
            }

            countSumNormTimeByShops();

            lblTimeMeasure.setText(newValue.getTimeName());
        });

        cmbxMaterial.valueProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedOperations){
                nc.setNormTime();;
            }
        });

        tfA.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedOperations){
                nc.setNormTime();
                countSumNormTimeByShops();
            }
        });

        tfB.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedOperations){
                nc.setNormTime();
                countSumNormTimeByShops();
            }
        });

    }

    private void countWeightAndArea() {
        try {
            ro = cmbxMaterial.getValue().getParamX();
            t = cmbxMaterial.getValue().getParamS();
            paramA = Double.parseDouble(tfA.getText().trim());
            paramB = Double.parseDouble(tfB.getText().trim());
            if(paramA <= 0 || paramB <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tfWeight.setText("");
            tfCoat.setText("");
            return;
        }

        double weight = t * paramA * paramB * ro * MM2_TO_M2 * 1.1;
        double area = 2 * paramA * paramB * MM2_TO_M2;

        tfWeight.setText(String.format(doubleFormat, weight));
        tfCoat.setText(String.format(doubleFormat, area));
    }


    /**
     * Метод расчитывает суммарное время по участкам
     */
    public void countSumNormTimeByShops(){
        double mechanicalTime = 0.0;
        double paintingTime = 0.0;
        for(AbstractOpPlate cn: addedOperations){
            if(cn.getNormType().equals(ENormType.NORM_MECHANICAL))
                mechanicalTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_PAINTING))
                paintingTime += cn.getCurrentNormTime();
        }

        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)){
            mechanicalTime = mechanicalTime * MIN_TO_SEC;
            paintingTime = paintingTime * MIN_TO_SEC;
        }

        String format = doubleFormat;
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)) format = integerFormat;

        tfMechanicalTime.setText(String.format(format, mechanicalTime));
        tfPaintingTime.setText(String.format(format, paintingTime));


        tfTotalTime.setText(String.format(format, mechanicalTime + paintingTime ));

    }

}
