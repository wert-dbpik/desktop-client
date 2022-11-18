package ru.wert.datapik.chogori.calculator.part_calculator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.common.components.BXPaintingDifficulty;
import ru.wert.datapik.chogori.common.components.TFInteger;

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

    private PartCalculatorController controller;

    private int razvParamA; //Параметр А развертки
    private int razvParamB; //Параметр B развертки
    private int paramC; //Параметр А - габарит сложенной детали
    private int paramD; //Параметр B - габарит сложенной детали
    private double s; //Площадь развертки
    private double difficulty; //Сложность окрашивания
    private double holdingTime; //Время навешивания
    private ETimeMeasurement measure; //Ед. измерения нормы времени

    public void init(PartCalculatorController controller){
        this.controller = controller;
        new BXPaintingDifficulty().create(cmbxDifficulty);

        setZeroValues();
        setNormTime();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        tfA.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfB.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfHangingTime.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        cmbxDifficulty.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedOperations().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countTotalNormTime();
        });

    }

    /**
     * Метод устанавливает расчитанную норму
     */
    @Override
    public void setNormTime() {
        tfNormTime.setText(String.valueOf(countNorm()));
        controller.countTotalNormTime();
    }

    @Override//AbstractNormsCounter
    public double countNorm(){

        boolean res = countInitialValues();
        if(!res) return 0.0;

        final int DELTA = 100; //расстояние между деталями

        final double WASHING = 12/60.0; //мойка, мин
        final double WINDING = 6/60.0; //продувка, мин
        final double DRYING = 20/60.0; //сушка, мин
        final double HOLDING_TIME = holdingTime/60.0; //время навешивания, мин

        final int minSize = Math.min(paramC, paramD) + DELTA;
        final int maxSize = Math.max(paramC, paramD) + DELTA;

        //Количество штанг в сушилке
        int dryingBars;
        if(maxSize < 99) dryingBars = 3;
        else if(maxSize >= 100 && maxSize <= 300) dryingBars = 2;
        else dryingBars = 1;

        int partsOnBar = Math.abs(2500/minSize);

        //Количество штанг в печи
        int bakeBars;
        if(maxSize < 49) bakeBars = 6;
        else if(maxSize >= 50 && maxSize <= 99) bakeBars = 5;
        else if(maxSize >= 100 && maxSize <= 199) bakeBars = 4;
        else if(maxSize >= 200 && maxSize <= 299) bakeBars = 3;
        else if(maxSize >= 300 && maxSize <= 399) bakeBars = 2;
        else bakeBars = 1;

        double time;
        time = HOLDING_TIME //Время навешивания
                + (WASHING + WINDING + DRYING/dryingBars)/partsOnBar //Время подготовки к окрашиванию
                + Math.pow(2*s, 0.7) * difficulty //Время нанесения покрытия
                + 40.0/bakeBars/partsOnBar;  //Время полимеризации

        currentNormTime = time;//результат в минутах
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        measure = controller.getCmbxTimeMeasurement().getValue();
        razvParamA = new TFInteger(controller.getTfA()).getIntegerValue();
        razvParamB = new TFInteger(controller.getTfB()).getIntegerValue();
        tfA.setText("0");
        tfB.setText(String.valueOf(Math.min(razvParamA, razvParamB)));
        tfHangingTime.setText("20");
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {
        try {
            razvParamA = Integer.parseInt(controller.getTfA().getText().trim());
            razvParamB = Integer.parseInt(controller.getTfB().getText().trim());
            if (razvParamA == 0.0 || razvParamB == 0.0) return false;
            s = razvParamA * razvParamB * MM2_TO_M2;

            paramC = Integer.parseInt(tfA.getText().trim());
            paramD = Integer.parseInt(tfB.getText().trim());
            if(paramC == 0 && paramD == 0) {
                paramC = Math.min(razvParamA, razvParamB);
                paramD = 0;
            }
            difficulty = cmbxDifficulty.getValue().getDifficultyRatio();
            holdingTime = Integer.parseInt(tfHangingTime.getText().trim());
            measure = controller.getCmbxTimeMeasurement().getValue();
        } catch (NumberFormatException e) {
            tfNormTime.setText("");
        }
        return true;
    }

}
