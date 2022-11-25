package ru.wert.datapik.chogori.calculator.controllers;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.*;
import ru.wert.datapik.chogori.calculator.entities.OpDetail;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.utils.DoubleParser;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;
import java.util.ArrayList;

public class PlateDetailController implements IOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_DETAIL;

    @FXML
    private TextField tfName;

    @FXML
    private ImageView ivEdit;

    @FXML
    private ImageView ivCopy;

    @FXML
    private ImageView ivDeleteOperation;
    
    @FXML
    private Label lblOperationName;

    private IFormMenu controller;
    private FormPartController partController;
    private OpDetail opData;
    public void setOpData(OpDetail opData){
        this.opData = opData;
    }

    @Override //IOpData
    public OpData getOpData(){
        return opData;
    }


    public void init(IFormMenu controller){
        this.controller = controller;
        lblOperationName.setStyle("-fx-text-fill: saddlebrown");
        opData = new OpDetail();

        ivEdit.setOnMouseClicked(e->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/calculatorPart.fxml"));
                Parent parent = loader.load();
                parent.setId("calculator");
                FormPartController partController = loader.getController();
                partController.init(tfName, opData);
                Stage stage =(Stage) parent.getScene().getWindow();
                stage.setOnCloseRequest(this::collectOpData);
                new WindowDecoration("Добавить деталь", parent, false, (Stage)lblOperationName.getScene().getWindow());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedOperations().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);

            controller.countSumNormTimeByShops();
        });

    }


    private void collectOpData(Event event){
        opData.setName(partController.getTfPartName().getText());
        opData.setMaterial(partController.getCmbxMaterial().getValue());
        opData.setParamA(IntegerParser.getValue(partController.getTfA()));
        opData.setParamB(IntegerParser.getValue(partController.getTfB()));
        opData.setOperations(new ArrayList<>());

        opData.setMechTime(DoubleParser.getValue(partController.getTfMechanicalTime()));
        opData.setPaintTime(DoubleParser.getValue(partController.getTfPaintingTime()));

    }




}
