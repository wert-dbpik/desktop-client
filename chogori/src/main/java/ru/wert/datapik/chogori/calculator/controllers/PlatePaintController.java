package ru.wert.datapik.chogori.calculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.interfaces.IFormController;
import ru.wert.datapik.chogori.calculator.controllers.forms.FormDetailController;
import ru.wert.datapik.chogori.calculator.components.BXPaintingDifficulty;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.entities.OpPaint;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlatePaintController extends AbstractOpPlate {

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfAlong;

    @FXML
    private TextField tfAcross;

    @FXML
    private ComboBox<EPaintingDifficulty> cmbxDifficulty;

    @FXML
    private ImageView ivHelpOnA;

    @FXML
    private TextField tfHangingTime;

    @FXML
    private TextField tfNormTime;

    private IFormController controller;
    private FormDetailController partController;
    private OpPaint opData;

    public OpData getOpData(){
        return opData;
    }


    private int razvA; //Параметр А развертки
    private int razvB; //Параметр B развертки
    private int along; //Параметр А - габарит сложенной детали вдоль штанги
    private int across; //Параметр B - габарит сложенной детали поперек штанги
    private double area; //Площадь развертки
    private double difficulty; //Сложность окрашивания
    private int hangingTime; //Время навешивания
    private ETimeMeasurement measure; //Ед. измерения нормы времени

    public void init(IFormController controller, OpPaint opData){
        this.controller = controller;
        this.partController = (FormDetailController)controller;
        this.opData = opData;

        new BXPaintingDifficulty().create(cmbxDifficulty);

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new TFColoredInteger(tfAlong, this);
        new TFColoredInteger(tfAcross, this);
        new TFColoredInteger(tfHangingTime, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        cmbxDifficulty.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countSumNormTimeByShops();
        });

        controller.getAddedPlates().add(this);
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

        final int DELTA = 100; //расстояние между деталями

        final double WASHING = 12/60.0; //мойка, мин
        final double WINDING = 6/60.0; //продувка, мин
        final double DRYING = 20/60.0; //сушка, мин
        final double HOLDING_TIME = hangingTime /60.0; //время навешивания, мин

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
        collectOpData();
    }


    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        razvA = IntegerParser.getValue(partController.getTfA());
        razvB = IntegerParser.getValue(partController.getTfB());

        area = razvA * razvB * MM2_TO_M2;

        along = IntegerParser.getValue(tfAlong);
        across = IntegerParser.getValue(tfAcross);
        if (along == 0 && across == 0) {
            along = Math.min(razvA, razvB);
            across = 0;
        }
        difficulty = cmbxDifficulty.getValue().getDifficultyRatio();
        hangingTime = IntegerParser.getValue(tfHangingTime);
        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setAlong(along);
        opData.setAcross(across);
        opData.setDifficulty(cmbxDifficulty.getValue());
        opData.setHangingTime(hangingTime);

        opData.setPaintTime(currentNormTime);
    }

    private void fillOpData(){
        along = opData.getAlong();
        tfAlong.setText(String.valueOf(along));

        across = opData.getAcross();
        tfAcross.setText(String.valueOf(across));

        difficulty = opData.getDifficulty().getDifficultyRatio();
        cmbxDifficulty.setValue(opData.getDifficulty());

        hangingTime = opData.getHangingTime();
        tfHangingTime.setText(String.valueOf(hangingTime));
    }

}
