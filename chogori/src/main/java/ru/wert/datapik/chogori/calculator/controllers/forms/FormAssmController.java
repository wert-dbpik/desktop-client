package ru.wert.datapik.chogori.calculator.controllers.forms;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.*;
import ru.wert.datapik.chogori.calculator.components.BXTimeMeasurement;
import ru.wert.datapik.chogori.calculator.entities.*;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.interfaces.IFormController;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.calculator.AbstractOpPlate.*;

public class FormAssmController implements IFormController {

    @FXML
    private TextField tfAssmName;

    @FXML @Getter
    private ComboBox<ETimeMeasurement> cmbxTimeMeasurement;

    @FXML @Getter
    private ListView<VBox> listViewTechOperations;

    @FXML
    private ImageView ivAddOperation;

    @FXML
    private ImageView ivErase;

    @FXML
    private ImageView ivHelpOnTechnologicalProcessing;

    @FXML
    private TextField tfMechanicalTime;

    @FXML
    private TextField tfPaintingTime;

    @FXML
    private TextField tfAssemblingTime;

    @FXML
    private TextField tfPackingTime;

    @FXML
    private Label lblTimeMeasure;

    private MenuCalculator menu;

    @FXML @Getter
    private TextField tfTotalTime;

    @Getter private ObservableList<AbstractOpPlate> addedPlates;
    @Getter private List<OpData> addedOperations;

    private IFormController controller;

    private OpAssm opData;

    @Override
    public void init(IFormController controller, TextField tfName, OpData opData) {
        this.opData = (OpAssm) opData;
        this.controller = controller;

        //Инициализируем список операционных плашек
        addedPlates = FXCollections.observableArrayList();
        addedOperations = new ArrayList<>();

        //Инициализируем наименование
        if(tfName != null) {
            tfAssmName.setText(tfName.getText());
            tfAssmName.textProperty().bindBidirectional(tfName.textProperty());
        }

        //Заполняем поля формы
        fillOpData();

        //Инициализируем комбобоксы
        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        initViews();
        createMenu();
    }

    private void fillOpData(){
        if(!opData.getOperations().isEmpty())
            deployData(opData);
    }

    private void initViews() {

        cmbxTimeMeasurement.valueProperty().addListener((observable, oldValue, newValue) -> {
            for(AbstractOpPlate nc : addedPlates){
                nc.setTimeMeasurement(newValue);
            }

            countSumNormTimeByShops();

            lblTimeMeasure.setText(newValue.getTimeName());
        });

    }

    private void createMenu() {

        MenuCalculator menu = new MenuCalculator(this, addedPlates, listViewTechOperations, addedOperations);

        menu.getItems().add(menu.createItemAddDetail());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.createItemAddWeldLongSeam(), menu.createItemAddWeldingDotted());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.createItemAddPaintAssm());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.createItemAddAssmNuts(), menu.createItemAddAssmCuttings(), menu.createItemAddAssmNodes());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(menu.createItemAddLevelingSealer());

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });
    }


    /**
     * Метод расчитывает суммарное время по участкам
     */
    @Override //IFormController
    public void countSumNormTimeByShops(){
        double mechanicalTime = 0.0;
        double paintingTime = 0.0;
        double assemblingTime = 0.0;
        double packingTime = 0.0;

        for(OpData cn: addedOperations){
            System.out.println(cn);
            System.out.println(cn.getMechTime());
            System.out.println(cn.getPaintTime());

            mechanicalTime += cn.getMechTime();
            paintingTime += cn.getPaintTime();
            assemblingTime += cn.getAssmTime();
            packingTime += cn.getPackTime();
        }

        if (controller != null)
            controller.countSumNormTimeByShops();

        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)){
            mechanicalTime = mechanicalTime * MIN_TO_SEC;
            paintingTime = paintingTime * MIN_TO_SEC;
            assemblingTime = assemblingTime * MIN_TO_SEC;
            packingTime = packingTime * MIN_TO_SEC;
        }

        String format = doubleFormat;
        if(cmbxTimeMeasurement.getValue().equals(ETimeMeasurement.SEC)) format = integerFormat;

        tfMechanicalTime.setText(String.format(format, mechanicalTime));
        tfPaintingTime.setText(String.format(format, paintingTime));
        tfAssemblingTime.setText(String.format(format, assemblingTime));
        tfPackingTime.setText(String.format(format, packingTime));

        tfTotalTime.setText(String.format(format, mechanicalTime + paintingTime + assemblingTime + packingTime));

    }

    private void deployData(OpAssm opData) {
        List<OpData> operations = opData.getOperations();
        for (OpData op : operations) {
            switch (op.getOpType()) {
                case CUTTING:
                    menu.addCattingPlate((OpCutting) op);
                    break;
                case BENDING:
                    menu.addBendingPlate((OpBending) op);
                    break;
                case LOCKSMITH:
                    menu.addLocksmithPlate((OpLocksmith) op);
                    break;
                case PAINTING:
                    menu.addPaintPlate((OpPaint) op);
                    break;
                case PAINTING_ASSM:
                    menu.addPaintAssmPlate((OpPaintAssm) op);
                    break;
                case WELD_CONTINUOUS:
                    menu.addWeldContinuousPlate((OpWeldContinuous) op);
                    break;
                case WELD_DOTTED:
                    menu.addWeldDottedPlate((OpWeldDotted) op);
                    break;
                case ASSM_CUTTINGS:
                    menu.addAssmCuttingsPlate((OpAssmCutting) op);
                    break;
                case ASSM_NUTS:
                    menu.addAssmNutsPlate((OpAssmNut) op);
                    break;
                case ASSM_NODES:
                    menu.addAssmNodesPlate((OpAssmNode) op);
                    break;
                case LEVELING_SEALER:
                    menu.addLevelingSealerPlate((OpLevelingSealer) op);
                    break;
            }
        }
    }

}
