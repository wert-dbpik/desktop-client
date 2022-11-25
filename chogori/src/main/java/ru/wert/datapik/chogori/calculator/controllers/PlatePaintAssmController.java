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
import ru.wert.datapik.chogori.calculator.IMenuCalculator;
import ru.wert.datapik.chogori.calculator.components.BXAssemblingType;
import ru.wert.datapik.chogori.calculator.components.TFColoredDouble;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.EAssemblingType;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.DoubleParser;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlatePaintAssmController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_PAINTING;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private ComboBox<EAssemblingType> cmbxAssemblingType;

    @FXML
    private TextField tfArea;

    @FXML
    private TextField tfAlong;

    @FXML
    private TextField tfAcross;

    @FXML
    private ImageView ivHelpOnPainting;

    @FXML
    private TextField tfNormTime;

    private IMenuCalculator controller;

    private int along; //Параметр А вдоль штанги
    private int across; //Параметр B поперек штанги
    private double area; //Площадь развертки
    private double pantingSpeed;// Скорость нанесения покрытия
    private ETimeMeasurement measure; //Ед. измерения нормы времени

    public void init(IMenuCalculator controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        new BXAssemblingType().create(cmbxAssemblingType);

        setZeroValues();
        setNormTime();

        new TFColoredDouble(tfArea, this);
        new TFColoredInteger(tfAlong, this);
        new TFColoredInteger(tfAcross, this);


        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        cmbxAssemblingType.valueProperty().addListener((observable, oldValue, newValue) -> {
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

    @Override//AbstractOpPlate
    public double countNorm(){

        countInitialValues();

        final int DELTA = 300; //расстояние между сборками

        final double HANGING_TIME = 0.34; //ремя навески и снятия после полимеризации
        final double WINDING_MOVING_SPEED = 1.4; //продувка после промывки и перемещение изделя на штанге, мин/1 м.кв.

        final int alongSize = along + DELTA;
        final int acrossSize = across + DELTA;

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
        time = HANGING_TIME//Время навешивания
                + area * WINDING_MOVING_SPEED //Время подготовки к окрашиванию
                + area * pantingSpeed //Время нанесения покрытия
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
        tfArea.setText("0.0");
        tfAlong.setText("0");
        tfAcross.setText("0");

        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {
        measure = controller.getCmbxTimeMeasurement().getValue();
        area = DoubleParser.getValue(tfArea);
        along = IntegerParser.getValue(tfAlong);
        across = IntegerParser.getValue(tfAcross);
        pantingSpeed = cmbxAssemblingType.getValue().getSpeed();

    }

}
