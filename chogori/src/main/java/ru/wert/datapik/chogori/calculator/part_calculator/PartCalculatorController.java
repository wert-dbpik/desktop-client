package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.chogori.calculator.INormsCounter;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.common.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartCalculatorController{

    @FXML @Getter
    private ComboBox<Material> cmbxMaterial;

    @FXML @Getter
    private ComboBox<ETimeMeasurement> cmbxTimeMeasurement;

    @FXML
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

    private List<INormsCounter> addedOperations;

//    public void init(){
//        addedOperations = new ArrayList<>();
//    }

    @FXML
    void initialize(){

        addedOperations = new ArrayList<>();

        new BXMaterial().create(cmbxMaterial);
        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        ContextMenu menu = createOperationsMenu();

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });

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
        for(INormsCounter cn: addedOperations){
            if(cn.getClass().getSimpleName().equals(clazz))
                return true;
        }
        return false;
    }

}
