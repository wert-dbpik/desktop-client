package ru.wert.datapik.chogori.calculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.IMenuCalculator;
import ru.wert.datapik.chogori.calculator.PartCalculatorController;
import ru.wert.datapik.chogori.calculator.components.BXPaintingDifficulty;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.components.TFInteger;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PaintingController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_PAINTING;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfA;

    @FXML
    private TextField tfB;

    @FXML
    private ComboBox<EPaintingDifficulty> cmbxDifficulty;

    @FXML
    private ImageView ivHelpOnDifficulty;

    @FXML
    private ImageView ivHelpOnA;

    @FXML
    private ImageView ivHelpOnB;

    @FXML
    private ImageView ivHelpOnHangingTime;



    @FXML
    private TextField tfHangingTime;

    @FXML
    private TextField tfNormTime;

    private IMenuCalculator controller;

    private int razvA; //Параметр А развертки
    private int razvB; //Параметр B развертки
    private int along; //Параметр А - габарит сложенной детали вдоль штанги
    private int across; //Параметр B - габарит сложенной детали поперек штанги
    private double area; //Площадь развертки
    private double difficulty; //Сложность окрашивания
    private double holdingTime; //Время навешивания
    private ETimeMeasurement measure; //Ед. измерения нормы времени

    public void init(IMenuCalculator controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        new BXPaintingDifficulty().create(cmbxDifficulty);

        setZeroValues();
        setNormTime();

        new TFColoredInteger(tfA, this);
        new TFColoredInteger(tfB, this);
        new TFColoredInteger(tfHangingTime, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        cmbxDifficulty.valueProperty().addListener((observable, oldValue, newValue) -> {
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

        final int DELTA = 100; //расстояние между деталями

        final double WASHING = 12/60.0; //мойка, мин
        final double WINDING = 6/60.0; //продувка, мин
        final double DRYING = 20/60.0; //сушка, мин
        final double HOLDING_TIME = holdingTime/60.0; //время навешивания, мин

        final int alongSize = Math.max(along, across) + DELTA;
        final int acrossSize = Math.min(along, across) + DELTA;

        //Количество штанг в сушилке
        int dryingBars;
        if(acrossSize < 99) dryingBars = 3;
        else if(acrossSize >= 100 && acrossSize <= 300) dryingBars = 2;
        else dryingBars = 1;

        int partsOnBar = 2500/alongSize;

        //Количество штанг в печи
        int bakeBars;
        if(acrossSize < 49) bakeBars = 6;
        else if(acrossSize >= 50 && acrossSize <= 99) bakeBars = 5;
        else if(acrossSize >= 100 && acrossSize <= 199) bakeBars = 4;
        else if(acrossSize >= 200 && acrossSize <= 299) bakeBars = 3;
        else if(acrossSize >= 300 && acrossSize <= 399) bakeBars = 2;
        else bakeBars = 1;

        double time;
        time = HOLDING_TIME //Время навешивания
                + (WASHING + WINDING + DRYING/dryingBars)/partsOnBar //Время подготовки к окрашиванию
                + Math.pow(2* area, 0.7) * difficulty //Время нанесения покрытия
                + 40.0/bakeBars/partsOnBar;  //Время полимеризации
        if(area == 0.0) time = 0.0;

        currentNormTime = time;//результат в минутах
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        measure = controller.getCmbxTimeMeasurement().getValue();
        razvA = new TFInteger(controller.getTfA()).getIntegerValue();
        razvB = new TFInteger(controller.getTfB()).getIntegerValue();
        tfA.setText(String.valueOf(Math.min(razvA, razvB)));
        tfB.setText("0");

        tfHangingTime.setText("20");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {

        razvA = IntegerParser.getValue(controller.getTfA());
        razvB = IntegerParser.getValue(controller.getTfB());

        area = razvA * razvB * MM2_TO_M2;

        along = IntegerParser.getValue(tfA);
        across = IntegerParser.getValue(tfB);
        if (along == 0 && across == 0) {
            along = Math.min(razvA, razvB);
            across = 0;
        }
        difficulty = cmbxDifficulty.getValue().getDifficultyRatio();
        holdingTime = IntegerParser.getValue(tfHangingTime);
        measure = controller.getCmbxTimeMeasurement().getValue();

        return true;
    }

}
