package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IMenuCalculator;
import ru.wert.datapik.chogori.calculator.components.BXPartBigness;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class WeldingContinuousController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfSeamLength;

    @FXML
    private CheckBox chbxUseStripping;

    @FXML
    private CheckBox chbxUseNumOfSeams;

    @FXML
    private Label lblNumOfSeams;

    @FXML
    private TextField tfNumOfSeams;

    @FXML
    private TextField tfNumOfMen;

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

    private IMenuCalculator controller;

    private int seamLength; //Длина шва
    private int seams; //Количество швов расчетное
    private int numOfSeams; //Количество швов заданное пользователем
    private int numOfMen; //Число человек, работающих над операцией
    private boolean useStripping; //Использовать зачистку
    private int connectionLength; //Длина сединения на которую расчитывается количество точек
    private int step; //шаг точек
    private double assemblingTime; //Время сборки свариваемого узла
    private ETimeMeasurement measure;

    public void init(IMenuCalculator controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        new BXPartBigness().create(cmbxPartBigness);
        setZeroValues();
        setNormTime();

        new TFColoredInteger(tfSeamLength, this);
        new TFColoredInteger(tfNumOfSeams, this);
        new TFColoredInteger(tfNumOfMen, this);
        new TFColoredInteger(tfConnectionLength, this);
        new TFColoredInteger(tfStep, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        chbxUseStripping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        cmbxPartBigness.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        chbxUseNumOfSeams.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                enableNumOfSeams();
            } else {
                enableNumOfSeamsCounting();
            }
            setNormTime();
        });

        ivDeleteOperation.setOnMousePressed(e->{
            controller.getAddedOperations().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countSumNormTimeByShops();
        });
    }

    private void enableNumOfSeams() {
        lblNumOfSeams.setDisable(false);
        tfNumOfSeams.setDisable(false);
        tfNumOfSeams.setText("1");
        disableNumOfSeamsCounting();
    }

    private void disableNumOfSeams() {
        lblNumOfSeams.setDisable(true);
        tfNumOfSeams.setDisable(true);
        tfNumOfSeams.setText("0");
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

    @Override//AbstractNormsCounter
    public double countNorm(){

        countInitialValues();

        final double WELDING_SPEED = 4.0; //скорость сваркм, мин/гиб

        if(numOfSeams == 0){
            if(step == 0){ //Деление на ноль
                return 0.0;
            } else
                seams = connectionLength/step; //Получаем целу часть от деления
        } else {
            seams = numOfSeams;
        }

        int sumWeldLength = seams * seamLength;


        double strippingTime;
        if(useStripping) {
            //Время на зачистку, мин
            if (sumWeldLength < 100) strippingTime = 0.5;
            else if (sumWeldLength >= 100 && sumWeldLength < 500) strippingTime = 1.8;
            else if (sumWeldLength >= 500 && sumWeldLength < 1000) strippingTime = 3.22;
            else strippingTime = sumWeldLength * MM_TO_M * 3.22;
        } else
            strippingTime = 0.0;


        double time;
        time =  numOfMen * (sumWeldLength * MM_TO_M * WELDING_SPEED + assemblingTime) + strippingTime;   //мин
        if(sumWeldLength == 0.0) time = 0.0;

        currentNormTime = time;
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfSeamLength.setText("0");
        tfNumOfMen.setText("1");
        tfNumOfSeams.setText("1");
        tfSeamLength.setText("0");
        enableNumOfSeams();
        disableNumOfSeamsCounting();
        chbxUseNumOfSeams.setSelected(true);
        chbxUseStripping.setSelected(false);
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        seamLength = IntegerParser.getValue(tfSeamLength);
        numOfSeams = IntegerParser.getValue(tfNumOfSeams);
        numOfMen = IntegerParser.getValue(tfNumOfMen);
        connectionLength = IntegerParser.getValue(tfConnectionLength);
        step = IntegerParser.getValue(tfStep);
        useStripping = chbxUseStripping.isSelected();
        assemblingTime = cmbxPartBigness.getValue().getTime();
        measure = controller.getCmbxTimeMeasurement().getValue();
    }


}
