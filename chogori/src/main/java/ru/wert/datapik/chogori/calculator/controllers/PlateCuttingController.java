package ru.wert.datapik.chogori.calculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.FormPartController;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpCutting;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateCuttingController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfHoles;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfPerfHoles;

    @FXML
    private CheckBox chbxStripping;

    @FXML
    private TextField tfExtraPerimeter;

    @FXML
    private ImageView ivHelpOnUseStripping;

    private IFormMenu controller;
    private FormPartController partController;
    private OpCutting opData;

    public OpData getOpData(){
        return opData;
    }

    private double perimetre; //Периметр контура развертки
    private double area; //Площадь развертки
    private int extraPerimeter; //Дополнительный периметр обработки
    private double t; //Толщина материала
    private double paramA; //Параметр А развертки
    private double paramB; //Параметр B развертки
    private boolean stripping = false; //Применить зачистку
    private int holes; //Количество отверстий в развертке
    private int perfHoles; //Количество перфораций в развертке
    private ETimeMeasurement measure; //Ед. измерения нормы времени


    public void init(IFormMenu controller, OpCutting opData){
        this.controller = controller;
        this.partController = (FormPartController) controller;
        if(opData == null){
            this.opData = new OpCutting();
            setZeroValues();
        } else {
            this.opData = opData;
            fillOpData();
        }

        controller.getAddedOperations().add(this);
        setZeroValues();
        setNormTime();

        new TFColoredInteger(tfHoles, this);
        new TFColoredInteger(tfPerfHoles, this);
        new TFColoredInteger(tfExtraPerimeter, this);

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        chbxStripping.selectedProperty().addListener((observable, oldValue, newValue) -> {
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
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override//AbstractOpPlate
    public void setZeroValues(){
        tfHoles.setText("0");
        tfPerfHoles.setText("0");
        tfExtraPerimeter.setText("0");
        chbxStripping.setSelected(true);

        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());

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

        final double REVOLVER_SPEED = 0.057; //скорость вырубки одного элемента револьвером, мин/уд
        final double PERFORATION_SPEED = 0.007; //корость перфорирования, мин/уд
        final double CUTTING_SERVICE_RATIO = 1.22; //коэфффициент, учитывающий 22% времени на обслуживание при резке
        final double PLUS_LENGTH = extraPerimeter * MM_TO_M;

        double speed;
        //Скорость резания, м/мин
        if (t < 1.5) speed = 5.5;
        else if (t >= 1.5 && t < 2) speed = 5.0;
        else if (t >= 2 && t < 2.5 ) speed = 4.0;
        else if (t >= 2.5 && t < 3.0) speed = 3.0;
        else speed = 1.9;

        //Время зачистки
        double strippingTime; //мин
        if(stripping){
            strippingTime = ((perimetre + PLUS_LENGTH) * 2.5 + holes) / 60;
        } else
            strippingTime = 0.0;

        double time;

        time = ((perimetre + PLUS_LENGTH)/speed                 //Время на резку по периметру
                + 1.28 * area                              //Время подготовительное - заключительоне
                + REVOLVER_SPEED * holes                //Время на пробивку отверстий
                + PERFORATION_SPEED * perfHoles)        //Время на пробивку перфорации
                * CUTTING_SERVICE_RATIO
                + strippingTime;

        if(area == 0.0) time = 0.0;

        currentNormTime = time;//результат в минутах
        collectOpData();
    }


    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        paramA = IntegerParser.getValue(partController.getTfA());
        paramB = IntegerParser.getValue(partController.getTfB());
        t = partController.getCmbxMaterial().getValue().getParamS();
        perimetre = 2 * (paramA + paramB) * MM_TO_M;
        area = paramA * paramB * MM2_TO_M2;
        extraPerimeter = IntegerParser.getValue(tfExtraPerimeter);
        stripping = chbxStripping.isSelected();
        holes = IntegerParser.getValue(tfHoles);
        perfHoles = IntegerParser.getValue(tfPerfHoles);
        measure = controller.getCmbxTimeMeasurement().getValue();

    }

    private void collectOpData(){
        opData.setHoles(holes);
        opData.setPerfHoles(perfHoles);
        opData.setExtraPerimeter(extraPerimeter);
        opData.setStripping(stripping);

        opData.setMechTime(currentNormTime);
    }

    private void fillOpData(){
        tfHoles.setText(String.valueOf(opData.getHoles()));
        tfPerfHoles.setText(String.valueOf(opData.getHoles()));
        tfExtraPerimeter.setText(String.valueOf(opData.getHoles()));
        chbxStripping.setSelected(opData.isStripping());

    }

}
