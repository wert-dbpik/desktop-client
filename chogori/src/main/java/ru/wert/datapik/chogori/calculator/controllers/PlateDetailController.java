package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.*;
import ru.wert.datapik.chogori.calculator.controllers.forms.FormDetailController;
import ru.wert.datapik.chogori.calculator.entities.OpDetail;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.interfaces.IFormController;
import ru.wert.datapik.chogori.calculator.interfaces.IOpPlate;
import ru.wert.datapik.chogori.calculator.utils.DoubleParser;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;
import java.util.ArrayList;

public class PlateDetailController extends AbstractOpPlate implements IOpPlate {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfN;

    @FXML
    private ImageView ivEdit;

    @FXML
    private ImageView ivCopy;

    @FXML
    private ImageView ivDeleteOperation;
    
    @FXML
    private Label lblOperationName;

    @FXML
    private Label lblQuantity;

    //Переменные
    private int quantity;
    @Setter@Getter
    private double currentMechanicalNormTime;
    @Setter@Getter
    private double currentPaintNormTime;

    //Переменные для ИМЕНИ
    private static int nameIndex = 0;
    private String detailName;

    private IFormController controller;
    private FormDetailController partController;

    private OpDetail opData;
    public void setOpData(OpDetail opData){
        this.opData = opData;
    }

    @Override //IOpData
    public OpData getOpData(){
        return opData;
    }

    private ETimeMeasurement measure;

    public void init(IFormController controller, OpDetail opData){
        this.controller = controller;
        this.opData = opData;

        fillOpData();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");
        lblQuantity.setStyle("-fx-text-fill: #8b4513");

        if(opData.getName() == null &&
                tfName.getText() == null || tfName.getText().equals("")) {
            detailName = String.format("Деталь #%s", ++nameIndex);
            tfName.setText(detailName);
        }

        ivEdit.setOnMouseClicked(e->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/calculatorPart.fxml"));
                Parent parent = loader.load();
                parent.setId("calculator");
                partController = loader.getController();
                partController.init(controller, tfName, opData);
                WindowDecoration windowDecoration = new WindowDecoration("Добавить деталь", parent, false, (Stage)lblOperationName.getScene().getWindow());
                ImageView closer = windowDecoration.getImgCloseWindow();
                closer.setOnMousePressed(ev->collectOpData());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);

            controller.countSumNormTimeByShops();
        });

        controller.getAddedPlates().add(this);
        setNormTime();
    }

    /**
     * Метод устанавливает расчитанную норму
     */
    @Override
    public void setNormTime() {
        countNorm();
        if (partController != null)
            setTimeMeasurement(measure);

        //!!!!!!!!!!!!!!!!!!!!!!!!
        controller.countSumNormTimeByShops();

    }

    @Override//AbstractOpPlate
    public void countNorm(){

        countInitialValues();

        double mechanicalTime = 0;
        double paintTime = 0;
        for(OpData op : opData.getOperations()){
            mechanicalTime += op.getMechTime();
            paintTime += op.getPaintTime();
        }

        currentMechanicalNormTime = mechanicalTime * quantity;
        currentPaintNormTime = paintTime * quantity;
        collectOpData();
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {
        quantity = IntegerParser.getValue(tfN);
        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData() {
        if(partController != null){
            opData.setName(partController.getTfPartName().getText());
            opData.setMaterial(partController.getCmbxMaterial().getValue());
            opData.setParamA(IntegerParser.getValue(partController.getTfA()));
            opData.setParamB(IntegerParser.getValue(partController.getTfB()));
            opData.setOperations(new ArrayList<>(partController.getAddedOperations()));

            opData.setMechTime(DoubleParser.getValue(partController.getTfMechanicalTime()));
            opData.setPaintTime(DoubleParser.getValue(partController.getTfPaintingTime()));
        }
        opData.setQuantity(IntegerParser.getValue(tfN));
    }

    private void fillOpData(){
        tfName.setText(opData.getName());
        tfN.setText(String.valueOf(opData.getQuantity()));
    }


}
