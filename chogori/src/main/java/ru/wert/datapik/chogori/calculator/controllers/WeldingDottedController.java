package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.components.BXPartBigness;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class WeldingDottedController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfNumOfParts;

    @FXML
    private TextField tfNumOfDots;

    @FXML
    private TextField tfNumOfDrops;

    @FXML
    private TextField tfNormTime;

    private PartCalculatorController controller;

    private int parts; //Количество элементов
    private int dots; //Количество точек
    private int drops; //Количество прихваток
    private ETimeMeasurement measure;

    public void init(PartCalculatorController controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        setZeroValues();
        setNormTime();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        tfNumOfParts.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfNumOfDots.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfNumOfDrops.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedOperations().remove(this);
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

    @Override//AbstractNormsCounter
    public double countNorm(){

        countInitialValues();

        final double WELDING_PARTS_SPEED = 0.13; //скорость онденсаторной сварки точкой, мин/элемент
        final double WELDING_DOTTED_SPEED = 0.3; //скорость контактной сварки, мин/точку
        final double WELDING_DROP_SPEED = 0.07; //скорость сварки прихватками, мин/прихватку

        double time;
        time =  parts * WELDING_PARTS_SPEED + dots * WELDING_DOTTED_SPEED + drops * WELDING_DROP_SPEED;   //мин

        currentNormTime = time;
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfNumOfParts.setText("0");
        tfNumOfDots.setText("0");
        tfNumOfDrops.setText("0");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {

        parts = IntegerParser.getValue(tfNumOfParts);
        dots = IntegerParser.getValue(tfNumOfDots);
        drops = IntegerParser.getValue(tfNumOfDrops);
        measure = controller.getCmbxTimeMeasurement().getValue();

        return true;
    }


}
