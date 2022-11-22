package ru.wert.datapik.chogori.calculator.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.calculator.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import java.io.IOException;

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

            countSumNormTimeByShops();

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
                nc.setNormTime();
                countSumNormTimeByShops();
            }
        });

        tfB.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractNormsCounter nc : addedOperations){
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

    /**
     * МЕНЮ ====================================================================
     */
    @NotNull
    private ContextMenu createOperationsMenu() {
        //РАСКРОЙ И ЗАЧИСТКА
        ContextMenu menu = new ContextMenu();
        MenuItem addCutting = new MenuItem("Резка и зачистка");
        addCutting.setOnAction(event -> {
            if(isDuplicate(CuttingController.class.getSimpleName())) return ;
            addCattingOperation();
        });

        //ГИБКА
        MenuItem addBending = new MenuItem("Гибка");
        addBending.setOnAction(event -> {
            if(isDuplicate(BendingController.class.getSimpleName())) return ;
            addBendingOperation();
        });

        //СЛЕСАРНЫЕ РАБОТЫ
        MenuItem addLocksmith = new MenuItem("Слесарные операции");
        addLocksmith.setOnAction(event -> {
            if(isDuplicate(LocksmithController.class.getSimpleName())) return ;
            addLocksmithOperation();
        });

        //=======================================================================

        //СВАРКА НЕПРЕРЫВНАЯ
        MenuItem addWeldingLongSeam = new MenuItem("Сварка непрерывная");
        addWeldingLongSeam.setOnAction(event -> {
            addWeldingContinuousOperation();
        });

        //СВАРКА ТОЧЕЧНАЯ
        MenuItem addWeldingDotted = new MenuItem("Сварка точечная");
        addWeldingDotted.setOnAction(event -> {
            if(isDuplicate(WeldingDottedController.class.getSimpleName())) return ;
            addWeldingDottedOperation();
        });

        //=======================================================================

        //ПОКРАСКА
        MenuItem addPainting = new MenuItem("Покраска");
        addPainting.setOnAction(event -> {
            if(isDuplicate(PaintingController.class.getSimpleName())) return ;
            addPaintingOperation();
        });

        //=======================================================================
        //СБОРКА - КРЕПЕЖ
        MenuItem addAssemblingNuts = new MenuItem("Сборка крепежа");
        addAssemblingNuts.setOnAction(event -> {
            if(isDuplicate(AssemblingNutsController.class.getSimpleName())) return ;
            addAssemblingNutsOperation();
        });

        //СБОРКА - РАСКРОЙНЫЙ МАТЕРИАЛ
        MenuItem addAssemblingCuttings = new MenuItem("Сборка раскройного материала");
        addAssemblingCuttings.setOnAction(event -> {
            if(isDuplicate(AssemblingCuttingsController.class.getSimpleName())) return ;
            addAssemblingCuttingsOperation();
        });

        //СБОРКА СТАНДАРТНЫХ УЗЛОВ
        MenuItem addAssemblingNodes = new MenuItem("Сборка стандартных узлов");
        addAssemblingNodes.setOnAction(event -> {
            if(isDuplicate(AssemblingNodesController.class.getSimpleName())) return ;
            addAssemblingNodesOperation();
        });

        menu.getItems().addAll(addCutting, addBending, addLocksmith);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(addWeldingLongSeam, addWeldingDotted);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(addPainting);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(addAssemblingNuts, addAssemblingCuttings, addAssemblingNodes);

        return menu;
    }

    /**
     * РАСКРОЙ И ЗАЧИСТКА
     */
    private void addCattingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/cutting.fxml"));
            VBox cutting = loader.load();
            cutting.setId("calculator");
            CuttingController controller = loader.getController();
            controller.init(this);
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
            listViewTechOperations.getItems().add(bending);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СЛЕСАРНЫЕ ОПЕРАЦИИ
     */
    private void addLocksmithOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/locksmith.fxml"));
            VBox locksmith = loader.load();
            locksmith.setId("calculator");
            LocksmithController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(locksmith);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СВАРКА НЕПРЕРЫВНАЯ
     */
    private void addWeldingContinuousOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/weldingContinuous.fxml"));
            VBox weldingLongSeam = loader.load();
            weldingLongSeam.setId("calculator");
            WeldingContinuousController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(weldingLongSeam);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СВАРКА ТОЧЕЧНАЯ
     */
    private void addWeldingDottedOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/weldingDotted.fxml"));
            VBox weldingDotted = loader.load();
            weldingDotted.setId("calculator");
            WeldingDottedController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(weldingDotted);
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
            listViewTechOperations.getItems().add(painting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СБОРКА КРЕПЕЖА
     */
    private void addAssemblingNutsOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingNuts.fxml"));
            VBox assemblingNuts = loader.load();
            assemblingNuts.setId("calculator");
            AssemblingNutsController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(assemblingNuts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СБОРКА РАСКРОЙНОГО МАТЕРИАЛА
     */
    private void addAssemblingCuttingsOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingCuttings.fxml"));
            VBox assemblingCuttings = loader.load();
            assemblingCuttings.setId("calculator");
            AssemblingCuttingsController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(assemblingCuttings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СБОРКА СТАНДАРТНЫХ УЗЛОВ
     */
    private void addAssemblingNodesOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingNodes.fxml"));
            VBox assemblingNodes = loader.load();
            assemblingNodes.setId("calculator");
            AssemblingNodesController controller = loader.getController();
            controller.init(this);
            listViewTechOperations.getItems().add(assemblingNodes);
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

    /**
     * Метод расчитывает суммарное время по участкам
     */
    public void countSumNormTimeByShops(){
        double mechanicalTime = 0.0;
        double paintingTime = 0.0;
        double assemblingTime = 0.0;
        double packingTime = 0.0;
        for(AbstractNormsCounter cn: addedOperations){
            if(cn.getNormType().equals(ENormType.NORM_MECHANICAL))
                mechanicalTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_PAINTING))
                paintingTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_ASSEMBLING))
                assemblingTime += cn.getCurrentNormTime();
            else if(cn.getNormType().equals(ENormType.NORM_PACKING))
                packingTime += cn.getCurrentNormTime();
        }

        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)){
            mechanicalTime = mechanicalTime * MIN_TO_SEC;
            paintingTime = paintingTime * MIN_TO_SEC;
            assemblingTime = assemblingTime * MIN_TO_SEC;
        }

        String format = doubleFormat;
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)) format = integerFormat;

        tfMechanicalTime.setText(String.format(format, mechanicalTime));
        tfPaintingTime.setText(String.format(format, paintingTime));
        tfAssemblingTime.setText(String.format(format, assemblingTime));

        tfTotalTime.setText(String.format(format, mechanicalTime + paintingTime + assemblingTime + packingTime));

    }

}
