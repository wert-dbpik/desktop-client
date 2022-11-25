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
import ru.wert.datapik.chogori.calculator.*;
import ru.wert.datapik.chogori.calculator.components.BXBendingTool;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

public class PlateDetailController implements IOperation {

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

    private IMenuCalculator controller;


    public void init(IMenuCalculator controller){
        this.controller = controller;
        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        ivEdit.setOnMouseClicked(e->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/calculatorPart.fxml"));
                Parent parent = loader.load();
                parent.setId("calculator");
                CalculatorPartController partController = loader.getController();
                partController.init(tfName);
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




}
