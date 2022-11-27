package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.BXSealersWidth;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpLevelingSealer;
import ru.wert.datapik.chogori.calculator.enums.ESealersWidth;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateLevelingSealerController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_ASSEMBLING;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivHelpOnLevelingSealer;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private ComboBox<ESealersWidth> cmbxSealerWidth;

    @FXML
    private TextField tfA;

    @FXML
    private TextField tfB;

    @FXML
    private TextField tfCompA;

    @FXML
    private TextField tfCompB;

    @FXML
    private TextField tfNormTime;

    @FXML
    private Label lblNormTimeMeasure;

    private IFormMenu controller;
    private OpLevelingSealer opData;

    public OpData getOpData(){
        return opData;
    }

    private int paramA; //Размер А
    private int paramB;//Размер Б
    private double perimeter; //

    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpLevelingSealer opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        this.opData = opData;

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new BXSealersWidth().create(cmbxSealerWidth);
        new TFColoredInteger(tfA, this);
        new TFColoredInteger(tfB, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        cmbxSealerWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countSumNormTimeByShops();
        });

        setNormTime();
    }

    /**
     * Метод устанавливает расчитанную норму
     */
    @Override
    public void setNormTime() {
        countNorm();
        setTimeMeasurement(measure);
        controller.countSumNormTimeByShops();
    }

    @Override//AbstractOpPlate
    public void countNorm(){

        countInitialValues();

        final double TIME = 0.32; //ПЗ время, мин
        final double LEVELING_SPEED = 0.16; //скорость нанесения, м/мин
        double time;
        time =  perimeter * LEVELING_SPEED + TIME;  //мин

        if(perimeter == 0) time = 0.0;
        else {
            tfCompA.setText(String.format(doubleFormat, perimeter * cmbxSealerWidth.getValue().getCompA()));
            tfCompB.setText(String.format(doubleFormat, perimeter * cmbxSealerWidth.getValue().getCompB()));
        }

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfA.setText("0");
        tfB.setText("0");
        tfCompA.setText("0.0");
        tfCompB.setText("0.0");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {
        paramA = IntegerParser.getValue(tfA);
        paramB = IntegerParser.getValue(tfB);
        perimeter = 2 * (paramA + paramB) * MM_TO_M;
        measure = controller.getCmbxTimeMeasurement().getValue();
    }


    private void collectOpData(){
        opData.setSealersWidth(cmbxSealerWidth.getValue());
        opData.setParamA(paramA);
        opData.setParamB(paramB);

        opData.setAssmTime(currentNormTime);
    }

    private void fillOpData(){

        cmbxSealerWidth.setValue(opData.getSealersWidth());

        paramA = opData.getParamA();
        tfA.setText(String.valueOf(paramA));

        paramB = opData.getParamB();
        tfB.setText(String.valueOf(paramB));

    }
}
