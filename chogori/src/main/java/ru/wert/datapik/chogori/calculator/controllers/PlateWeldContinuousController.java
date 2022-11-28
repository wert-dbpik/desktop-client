package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.BXPartBigness;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpWeldContinuous;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateWeldContinuousController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfSeamLength;

    @FXML
    private CheckBox chbxStripping;

    @FXML
    private CheckBox chbxPreEnterSeams;

    @FXML
    private Label lblNumOfSeams;

    @FXML
    private TextField tfSeams;

    @FXML
    private TextField tfMen;

    @FXML
    private Label lblConnectionLength;

    @FXML
    private TextField tfConnectionLength;

    @FXML
    private Label lblStep;

    @FXML
    private TextField tfStep;

    @FXML
    private ComboBox<EPartBigness> cmbxPartBigness;

    @FXML
    private TextField tfNormTime;

    private IFormMenu controller;
    private OpWeldContinuous opData;

    public OpData getOpData(){
        return opData;
    }

    private int seamLength; //Длина шва
    private int seamsCounted; //Количество швов расчетное
    private int seams; //Количество швов заданное пользователем
    private int men; //Число человек, работающих над операцией
    private boolean stripping; //Использовать зачистку
    private int connectionLength; //Длина сединения на которую расчитывается количество точек
    private int step; //шаг точек
    private double assemblingTime; //Время сборки свариваемого узла
    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpWeldContinuous opData){
        this.controller = controller;
        this.opData = opData;

        new BXPartBigness().create(cmbxPartBigness);

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new TFColoredInteger(tfSeamLength, this);
        new TFColoredInteger(tfSeams, this);
        new TFColoredInteger(tfMen, this);
        new TFColoredInteger(tfConnectionLength, this);
        new TFColoredInteger(tfStep, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        chbxStripping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        cmbxPartBigness.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        chbxPreEnterSeams.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                enableNumOfSeams();
            } else {
                enableNumOfSeamsCounting();
            }
            setNormTime();
        });

        ivDeleteOperation.setOnMousePressed(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countSumNormTimeByShops();
        });

        controller.getAddedPlates().add(this);
        setNormTime();
    }

    private void enableNumOfSeams() {
        lblNumOfSeams.setDisable(false);
        tfSeams.setDisable(false);
        tfSeams.setText("1");
        disableNumOfSeamsCounting();
    }

    private void disableNumOfSeams() {
        lblNumOfSeams.setDisable(true);
        tfSeams.setDisable(true);
        tfSeams.setText("0");
    }

    private void disableNumOfSeamsCounting(){
        lblConnectionLength.setDisable(true);
        tfConnectionLength.setDisable(true);
        tfConnectionLength.setText("0");
        lblStep.setDisable(true);
        tfStep.setDisable(true);
        tfStep.setText("0");
    }

    private void enableNumOfSeamsCounting(){
        disableNumOfSeams();
        lblConnectionLength.setDisable(false);
        tfConnectionLength.setDisable(false);
        tfConnectionLength.setText("0");
        lblStep.setDisable(false);
        tfStep.setDisable(false);
        tfStep.setText("0");
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

        final double WELDING_SPEED = 4.0; //скорость сваркм, мин/гиб

        if(seams == 0){
            if(step == 0){ //Деление на ноль
                return;
            } else
                seamsCounted = connectionLength/step; //Получаем целу часть от деления
        } else {
            seamsCounted = seams;
        }

        int sumWeldLength = seamsCounted * seamLength;


        double strippingTime;
        if(stripping) {
            //Время на зачистку, мин
            if (sumWeldLength < 100) strippingTime = 0.5;
            else if (sumWeldLength >= 100 && sumWeldLength < 500) strippingTime = 1.8;
            else if (sumWeldLength >= 500 && sumWeldLength < 1000) strippingTime = 3.22;
            else strippingTime = sumWeldLength * MM_TO_M * 3.22;
        } else
            strippingTime = 0.0;


        double time;
        time =  men * (sumWeldLength * MM_TO_M * WELDING_SPEED + assemblingTime) + strippingTime;   //мин
        if(sumWeldLength == 0.0) time = 0.0;

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        seamLength = IntegerParser.getValue(tfSeamLength);
        seams = IntegerParser.getValue(tfSeams);
        men = IntegerParser.getValue(tfMen);
        connectionLength = IntegerParser.getValue(tfConnectionLength);
        step = IntegerParser.getValue(tfStep);
        stripping = chbxStripping.isSelected();
        assemblingTime = cmbxPartBigness.getValue().getTime();
        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setSeamLength(seamLength);
        opData.setPartBigness(cmbxPartBigness.getValue());
        opData.setMen(men);
        opData.setStripping(stripping);
        opData.setPreEnterSeams(chbxPreEnterSeams.isSelected());
        opData.setSeams(seams);
        opData.setConnectionLength(connectionLength);
        opData.setStep(step);

        opData.setMechTime(currentNormTime);
    }

    private void fillOpData(){
        seamLength = opData.getSeamLength();
        tfSeamLength.setText(String.valueOf(seamLength));

        cmbxPartBigness.setValue(opData.getPartBigness());

        men = opData.getMen();
        tfMen.setText(String.valueOf(men));

        stripping = opData.isStripping();
        chbxStripping.setSelected(stripping);

        chbxPreEnterSeams.setSelected(opData.isPreEnterSeams());

        seams = opData.getSeams();
        tfSeams.setText(String.valueOf(seams));

        connectionLength = opData.getConnectionLength();
        tfConnectionLength.setText(String.valueOf(connectionLength));

        step = opData.getStep();
        tfStep.setText(String.valueOf(step));

    }

}
