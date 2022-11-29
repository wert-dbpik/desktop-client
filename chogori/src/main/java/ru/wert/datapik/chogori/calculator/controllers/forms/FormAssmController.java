package ru.wert.datapik.chogori.calculator.controllers.forms;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import ru.wert.datapik.chogori.calculator.components.ObservableNormTime;
import ru.wert.datapik.chogori.calculator.entities.*;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

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
    @Getter ObjectProperty<Double> currentMechTime = new SimpleObjectProperty<>();
    @Getter ObjectProperty<Double> currentPaintTime = new SimpleObjectProperty<>();
    @Getter ObjectProperty<Double> currentAssmTime = new SimpleObjectProperty<>();
    @Getter ObjectProperty<Double> currentPackTime = new SimpleObjectProperty<>();

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

        //Инициализируем наблюдаемые переменные
        if(controller != null) {
            new ObservableNormTime(currentMechTime, controller);
            new ObservableNormTime(currentPaintTime, controller);
            new ObservableNormTime(currentAssmTime, controller);
            new ObservableNormTime(currentPackTime, controller);
        }

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

        menu.getItems().add(menu.getAddDetail());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddWeldLongSeam(), menu.getAddWeldingDotted());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddPaintingAssembling());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddAssemblingNuts(), menu.getAddAssemblingCuttings(), menu.getAddAssemblingNodes());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(menu.getAddLevelingSealer());

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
        for(AbstractOpPlate cn: addedPlates){
            mechanicalTime += cn.getOpData().getMechTime();
            paintingTime += cn.getOpData().getPaintTime();
            assemblingTime += cn.getOpData().getAssmTime();
            packingTime += cn.getOpData().getPackTime();
        }

        currentMechTime.set(mechanicalTime);
        currentPaintTime.set(paintingTime);
        currentAssmTime.set(assemblingTime);
        currentPackTime.set(packingTime);

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
                    menu.addCattingOperation((OpCutting) op);
                    break;
                case BENDING:
                    menu.addBendingOperation((OpBending) op);
                    break;
                case LOCKSMITH:
                    menu.addLocksmithOperation((OpLocksmith) op);
                    break;
                case PAINTING:
                    menu.addPaintOperation((OpPaint) op);
                    break;
                case PAINTING_ASSM:
                    menu.addPaintAssmOperation((OpPaintAssm) op);
                    break;
                case WELD_CONTINUOUS:
                    menu.addWeldContinuousOperation((OpWeldContinuous) op);
                    break;
                case WELD_DOTTED:
                    menu.addWeldDottedOperation((OpWeldDotted) op);
                    break;
                case ASSM_CUTTINGS:
                    menu.addAssmCuttingsOperation((OpAssmCutting) op);
                    break;
                case ASSM_NUTS:
                    menu.addAssmNutsOperation((OpAssmNut) op);
                    break;
                case ASSM_NODES:
                    menu.addAssmNodesOperation((OpAssmNode) op);
                    break;
                case LEVELING_SEALER:
                    menu.addLevelingSealerOperation((OpLevelingSealer) op);
                    break;
            }
        }
    }

}
