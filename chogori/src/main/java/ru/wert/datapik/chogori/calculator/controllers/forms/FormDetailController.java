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
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.IFormController;
import ru.wert.datapik.chogori.calculator.MenuCalculator;
import ru.wert.datapik.chogori.calculator.components.ObservableNormTime;
import ru.wert.datapik.chogori.calculator.entities.*;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.common.components.BXMaterial;
import ru.wert.datapik.chogori.calculator.components.BXTimeMeasurement;
import ru.wert.datapik.client.entity.models.Material;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.calculator.AbstractOpPlate.*;

public class FormDetailController implements IFormController {

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
    private OpDetail opData;

    @Getter ObjectProperty<Double> currentMechTime = new SimpleObjectProperty<>();
    @Getter ObjectProperty<Double> currentPaintTime = new SimpleObjectProperty<>();


    private double ro; //Плотность
    private double t; //Толщина
    private int paramA; //параметр А
    private int paramB; //параметр B

    @Getter private ObservableList<AbstractOpPlate> addedPlates;
    @Getter private List<OpData> addedOperations;
    private IFormController controller;

    @Override //IFormController
    public void init(IFormController controller, TextField tfName, OpData opData) {
        this.opData = (OpDetail) opData;
        this.controller = controller;

        //Инициализируем список операционных плашек
        addedPlates = FXCollections.observableArrayList();
        addedOperations = new ArrayList<>();

        //Инициализируем наименование
        tfPartName.setText(tfName.getText());
        tfName.textProperty().bindBidirectional(tfPartName.textProperty());

        //Инициализируем комбобоксы
        new BXMaterial().create(cmbxMaterial);
        new BXTimeMeasurement().create(cmbxTimeMeasurement);

        //Создаем меню
        createMenu();

        //Заполняем поля формы
        fillOpData();
        countWeightAndArea();

        //Инициализируем наблюдаемые переменные
        if(controller != null) {
            new ObservableNormTime(currentMechTime, controller);
            new ObservableNormTime(currentPaintTime, controller);
        }

        initViews();

    }

    private void initViews() {
        cmbxTimeMeasurement.valueProperty().addListener((observable, oldValue, newValue) -> {
            for(AbstractOpPlate nc : addedPlates){
                nc.setTimeMeasurement(newValue);
            }

            countSumNormTimeByShops();

            lblTimeMeasure.setText(newValue.getTimeName());
        });

        cmbxMaterial.valueProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedPlates){
                nc.setNormTime();;
            }
        });

        tfA.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedPlates){
                nc.setNormTime();
                countSumNormTimeByShops();
            }
        });

        tfB.textProperty().addListener((observable, oldValue, newValue) -> {
            countWeightAndArea();
            for(AbstractOpPlate nc : addedPlates){
                nc.setNormTime();
                countSumNormTimeByShops();
            }
        });
    }

    private void deployData(OpDetail opData) {
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

    private void createMenu(){
        menu = new MenuCalculator(this, addedPlates, listViewTechOperations, addedOperations);

        menu.getItems().addAll(menu.getAddCutting(), menu.getAddBending(), menu.getAddLocksmith());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddWeldLongSeam(), menu.getAddWeldingDotted());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().addAll(menu.getAddPainting());

        ivAddOperation.setOnMouseClicked(e->{
            menu.show(ivAddOperation, Side.LEFT, -15.0, 30.0);
        });
    }

    private void countWeightAndArea() {
        try {
            ro = cmbxMaterial.getValue().getParamX();
            t = cmbxMaterial.getValue().getParamS();
            paramA = Integer.parseInt(tfA.getText().trim());
            paramB = Integer.parseInt(tfB.getText().trim());
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
        for(AbstractOpPlate cn: addedPlates){
            mechanicalTime += cn.getOpData().getMechTime();
            paintingTime += cn.getOpData().getPaintTime();
        }

        currentMechTime.set(mechanicalTime);
        currentPaintTime.set(paintingTime);

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

    private void fillOpData(){

        if(opData.getMaterial() != null)
            cmbxMaterial.setValue(opData.getMaterial());

        paramA = opData.getParamA();
        tfA.setText(String.valueOf(paramA));

        paramB = opData.getParamB();
        tfB.setText(String.valueOf(paramB));

        if(!opData.getOperations().isEmpty())
            deployData(opData);
    }

}
