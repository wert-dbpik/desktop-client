package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.TFColoredDouble;
import ru.wert.datapik.chogori.calculator.entities.OpAssmCutting;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.DoubleParser;

public class PlateAssmCuttingsController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_ASSEMBLING;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfSealer;

    @FXML
    private TextField tfSelfAdhSealer;

    @FXML
    private TextField tfInsulation;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private Label lblOperationName;

    private IFormMenu controller;
    private OpAssmCutting opData;

    public OpData getOpData(){
        return opData;
    }

    private double sealer; //Уплотнитель на ребро корпуса
    private double selfAdhSealer; //Уплотнитель самоклеющийся
    private double insulation; //Утеплитель

    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpAssmCutting opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        if(opData == null){
            this.opData = new OpAssmCutting();
            setZeroValues();
        } else {
            this.opData = opData;
            fillOpData();
        }
        setNormTime();

        new TFColoredDouble(tfSealer, this);
        new TFColoredDouble(tfSelfAdhSealer, this);
        new TFColoredDouble(tfInsulation, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countSumNormTimeByShops();
        });
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

        final double SEALER_SPEED = 40 * SEC_TO_MIN; //скорость монтажа уплотнителя
        final double SELF_ADH_SEALER_SPEED =  20 * SEC_TO_MIN; //скорость наклейки уплотнителя
        final double INSULATION_SPEED = 5.5; //скорость разметки, резки и укладки уплотнителя

        double time;
        time =  sealer * SEALER_SPEED
                + selfAdhSealer * SELF_ADH_SEALER_SPEED
                + insulation * INSULATION_SPEED;//мин

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfSealer.setText("0");
        tfSelfAdhSealer.setText("0");
        tfInsulation.setText("0");

        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        sealer = DoubleParser.getValue(tfSealer);
        selfAdhSealer = DoubleParser.getValue(tfSelfAdhSealer);
        insulation = DoubleParser.getValue(tfInsulation);

        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setSealer(sealer);
        opData.setSelfAdhSealer(selfAdhSealer);
        opData.setInsulation(insulation);

        opData.setAssmTime(currentNormTime);
    }

    private void fillOpData(){
        tfSealer.setText(String.valueOf(opData.getSealer()));
        tfSelfAdhSealer.setText(String.valueOf(opData.getSelfAdhSealer()));
        tfInsulation.setText(String.valueOf(opData.getInsulation()));

        opData.setAssmTime(currentNormTime);
    }


}
