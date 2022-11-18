package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.common.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.calculator.AbstractNormsCounter.*;

public class PartCalculatorController{

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
    private StackPane spCuttingContainer;

    @FXML
    private StackPane spPaintingContainer;

    @FXML
    private StackPane spBendingContainer;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;

    @FXML
    private TextField tfMechanicalTime;

    @FXML
    private TextField tfPaintingTime;

    @FXML
    private TextField tfAssemblingTime;

    @FXML
    private Label lblTimeMeasure;

    @FXML @Getter
    private TextField tfTotalTime;

    private double ro; //Плотность
    private double t; //Толщина
    private double paramA; //параметр А
    private double paramB; //параметр B

    @Getter private ObservableList<AbstractNormsCounter> addedOperations;


    @FXML
    void initialize(){

        addedOperations = FXCollections.observableArrayList();
//        listViewTechOperations.setItems(addedOperations);

        new BXMaterial().create(cmbxMaterial);
        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        ContextMenu menu = createOperationsMenu();

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });

        cmbxTimeMeasurement.valueProperty().addListener((observable, oldValue, newValue) -> {
            for(AbstractNormsCounter nc : addedOperations){
                nc.setTimeMeasurement(newValue);
            }
            tfMechanicalTime.setText(String.valueOf(countTotalMechanicalTime()));
            tfPaintingTime.setText(String.valueOf(countTotalPaintingTime()));
            tfMechanicalTime.setText(String.valueOf(countTotalAssemblingTime()));
            tfTotalTime.setText(String.valueOf(countTotalTime()));

            lblTimeMeasure.setText(newValue.getTimeName());
        });

        cmbxMaterial.valueProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractNormsCounter nc : addedOperations){
                nc.setNormTime();;
            }
        });

        tfA.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractNormsCounter nc : addedOperations){
                nc.setNormTime();;
            }
        });

        tfB.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractNormsCounter nc : addedOperations){
                nc.setNormTime();;
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

        tfWeight.setText(String.valueOf(weight));
        tfCoat.setText(String.valueOf(area));
    }

    /**
     * Высчитывается общее время
     */
    private double countTotalTime() {
        double time = 0.0;
        for(AbstractNormsCounter nc : addedOperations){
            time += nc.getCurrentNormTime();
        }
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }

    /**
     * Высчитывается общее время на механические работы
     */
    private double countTotalMechanicalTime() {
        double time = 0.0;
        for(AbstractNormsCounter nc : addedOperations){
            if(nc.getNormType().equals(ENormType.NORM_MECHANICAL))
                time += nc.getCurrentNormTime();
        }
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }

    /**
     * Высчитывается общее время на окрасочные работы
     */
    private double countTotalPaintingTime() {
        double time = 0.0;
        for(AbstractNormsCounter nc : addedOperations){
            if(nc.getNormType().equals(ENormType.NORM_PAINTING))
                time += nc.getCurrentNormTime();
        }
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }

    /**
     * Высчитывается общее время на сборочные работы
     */
    private double countTotalAssemblingTime() {
        double time = 0.0;
        for(AbstractNormsCounter nc : addedOperations){
            if(nc.getNormType().equals(ENormType.NORM_ASSEMBLING))
                time += nc.getCurrentNormTime();
        }
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }

    @NotNull
    private ContextMenu createOperationsMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem addCutting = new MenuItem("Резка и зачистка");
        addCutting.setOnAction(event -> {
            if(isDuplicate(CuttingController.class.getSimpleName())) return ;
            addCattingOperation();
        });
        MenuItem addBending = new MenuItem("Гибка");
        addBending.setOnAction(event -> {
            if(isDuplicate(BendingController.class.getSimpleName())) return ;
            addBendingOperation();
        });
        MenuItem addPainting = new MenuItem("Покраска");
        addPainting.setOnAction(event -> {
            if(isDuplicate(PaintingController.class.getSimpleName())) return ;
            addPaintingOperation();
        });

        menu.getItems().addAll(addCutting, addBending, addPainting);
        return menu;
    }

    /**
     * РЕЗКА И ЗАЧИСТКА
     */
    private void addCattingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/cutting.fxml"));
            VBox cutting = loader.load();
            cutting.setId("calculator");
            CuttingController controller = loader.getController();
            controller.init(this);
            addedOperations.add(controller);
            listViewTechOperations.getItems().add(cutting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ГИБКА
     */
    private void addBendingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/bending.fxml"));
            VBox bending = loader.load();
            bending.setId("calculator");
            BendingController controller = loader.getController();
            controller.init(this);
            addedOperations.add(controller);
            listViewTechOperations.getItems().add(bending);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ПОКРАСКА
     */
    private void addPaintingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/painting.fxml"));
            VBox painting = loader.load();
            painting.setId("calculator");
            PaintingController controller = loader.getController();
            controller.init(this);
            addedOperations.add(controller);
            listViewTechOperations.getItems().add(painting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Ищем дубликат операции в списке addedOperations по clazz
     */
    private boolean isDuplicate(String clazz){
        for(AbstractNormsCounter cn: addedOperations){
            if(cn.getClass().getSimpleName().equals(clazz))
                return true;
        }
        return false;
    }

}
