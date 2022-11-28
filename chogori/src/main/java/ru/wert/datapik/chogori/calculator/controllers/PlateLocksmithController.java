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
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpLocksmith;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateLocksmithController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfCountersinkings;

    @FXML
    private TextField tfThreadings;

    @FXML
    private TextField tfRivets;

    @FXML
    private TextField tfSmallSawings;

    @FXML
    private TextField tfBigSawings;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private Label lblOperationName;

    private IFormMenu controller;
    private OpLocksmith opData;

    public OpData getOpData(){
        return opData;
    }

    private int rivets; //Количество заклепок
    private int countersinkings; //количество зенкуемых отверстий
    private int threadings; //Количество нарезаемых резьб
    private int smallSawings; //Количество резов на малой пиле
    private int bigSawings; //Количество резов на большой пиле
    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpLocksmith opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        this.opData = opData;

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new TFColoredInteger(tfRivets, this);
        new TFColoredInteger(tfCountersinkings, this);
        new TFColoredInteger(tfThreadings, this);
        new TFColoredInteger(tfSmallSawings, this);
        new TFColoredInteger(tfBigSawings, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

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

        final double RIVETS_SPEED = 18 * SEC_TO_MIN; //скорость установки вытяжной заклепки
        final double COUNTERSINKING_SPEED = 0.31; //скорость сверления и зенковки
        final double THREADING_SPEED = 0.37; //скорость нарезания резьбы
        final double SMALL_SAWING_SPEED = 0.2; //скорость пиления на малой пиле
        final double BIG_SAWING_SPEED = 1.0; //скорость пиления на большой пиле

        double time;
        time =  rivets * RIVETS_SPEED
                + countersinkings * COUNTERSINKING_SPEED
                + threadings * THREADING_SPEED
                + smallSawings * SMALL_SAWING_SPEED
                + bigSawings * BIG_SAWING_SPEED;   //мин

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
//    @Override
//    public void setZeroValues() {
//        tfRivets.setText("0");
//        tfCountersinkings.setText("0");
//        tfThreadings.setText("0");
//        tfSmallSawings.setText("0");
//        tfBigSawings.setText("0");
//        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
//    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        rivets = IntegerParser.getValue(tfRivets);
        countersinkings = IntegerParser.getValue(tfCountersinkings);
        threadings = IntegerParser.getValue(tfThreadings);
        smallSawings = IntegerParser.getValue(tfSmallSawings);
        bigSawings = IntegerParser.getValue(tfBigSawings);
        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setRivets(rivets);
        opData.setCountersinkings(countersinkings);
        opData.setThreadings(threadings);
        opData.setSmallSawings(smallSawings);
        opData.setBigSawings(bigSawings);

        opData.setMechTime(currentNormTime);
    }

    private void fillOpData(){
        rivets = opData.getRivets();
        tfRivets.setText(String.valueOf(rivets));

        countersinkings = opData.getCountersinkings();
        tfCountersinkings.setText(String.valueOf(countersinkings));

        threadings = opData.getThreadings();
        tfThreadings.setText(String.valueOf(threadings));

        smallSawings = opData.getSmallSawings();
        tfSmallSawings.setText(String.valueOf(smallSawings));

        bigSawings = opData.getBigSawings();
        tfBigSawings.setText(String.valueOf(bigSawings));
    }


}