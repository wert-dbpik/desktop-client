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
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpWeldDotted;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateWeldDottedController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfParts;

    @FXML
    private TextField tfDots;

    @FXML
    private TextField tfDrops;

    @FXML
    private TextField tfNormTime;

    private IFormMenu controller;
    private OpWeldDotted opData;

    public OpData getOpData(){
        return opData;
    }

    private int parts; //Количество элементов
    private int dots; //Количество точек
    private int drops; //Количество прихваток
    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpWeldDotted opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        this.opData = opData;

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        tfParts.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfDots.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfDrops.textProperty().addListener((observable, oldValue, newValue) -> {
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

        final double WELDING_PARTS_SPEED = 0.13; //скорость онденсаторной сварки точкой, мин/элемент
        final double WELDING_DOTTED_SPEED = 0.3; //скорость контактной сварки, мин/точку
        final double WELDING_DROP_SPEED = 0.07; //скорость сварки прихватками, мин/прихватку

        double time;
        time =  parts * WELDING_PARTS_SPEED + dots * WELDING_DOTTED_SPEED + drops * WELDING_DROP_SPEED;   //мин

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
//    @Override
//    public void setZeroValues() {
//        tfParts.setText("0");
//        tfDots.setText("0");
//        tfDrops.setText("0");
//        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
//    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        parts = IntegerParser.getValue(tfParts);
        dots = IntegerParser.getValue(tfDots);
        drops = IntegerParser.getValue(tfDrops);

        measure = controller.getCmbxTimeMeasurement().getValue();
        collectOpData();
    }

    private void collectOpData(){
        opData.setParts(parts);
        opData.setDots(dots);
        opData.setDrops(drops);

        opData.setMechTime(currentNormTime);
    }

    private void fillOpData(){
        parts = opData.getParts();
        tfParts.setText(String.valueOf(parts));

        dots = opData.getDots();
        tfDots.setText(String.valueOf(dots));

        drops = opData.getDrops();
        tfDrops.setText(String.valueOf(drops));
    }


}