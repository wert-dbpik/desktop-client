package ru.wert.datapik.chogori.calculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.controllers.forms.FormDetailController;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpCutting;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

/**
 * При создании класса создается экзепляр класса OpCutting
 * В этом классе хранятся все актуальные значения полей, введенные пользователем,
 * они обновляются при любом изменении полей плашки.
 */
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
    private FormDetailController partController;
    private OpCutting opData;

    public OpData getOpData(){
        return opData;
    }

    private double perimeter; //Периметр контура развертки
    private double area; //Площадь развертки
    private int extraPerimeter; //Дополнительный периметр обработки
    private double t; //Толщина материала
    private int paramA; //Параметр А развертки
    private int paramB; //Параметр B развертки
    private boolean stripping = false; //Применить зачистку
    private int holes; //Количество отверстий в развертке
    private int perfHoles; //Количество перфораций в развертке
    private ETimeMeasurement measure; //Ед. измерения нормы времени


    public void init(IFormMenu controller, OpCutting opData){
        this.controller = controller;
        this.partController = (FormDetailController) controller;
        this.opData = opData;

        new TFColoredInteger(tfHoles, this);
        new TFColoredInteger(tfPerfHoles, this);
        new TFColoredInteger(tfExtraPerimeter, this);

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        chbxStripping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0; //Обнуляем значение, чтобы вычесть его из суммарных норм
            controller.countSumNormTimeByShops();
        });

        controller.getAddedPlates().add(this);
        setNormTime();

    }

    /**
     * Метод вызывается для вычисления нормы времени на плашку и суммарного времени для формы
     */
    @Override
    public void setNormTime() {
        countNorm();
        setTimeMeasurement(measure);
        controller.countSumNormTimeByShops();
    }

    /**
     * Метод вызывается для пересчета норм времени при любом изменении значения полей плашки
     * Сначала  в методе countInitialValues() происходит сбор необходимых для расчета значений.
     * После выполненных вычислений переменная currentNormTime обновляется, и в методе collectOpData() значения полей
     * плашки вместе с полученным значением нормы времени сохраняются в класс OpData
     */
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
            strippingTime = ((perimeter + PLUS_LENGTH) * 2.5 + holes) / 60;
        } else
            strippingTime = 0.0;

        double time;

        time = ((perimeter + PLUS_LENGTH)/speed                 //Время на резку по периметру
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
        perimeter = 2 * (paramA + paramB) * MM_TO_M;
        area = paramA * paramB * MM2_TO_M2;
        extraPerimeter = IntegerParser.getValue(tfExtraPerimeter);
        stripping = chbxStripping.isSelected();
        holes = IntegerParser.getValue(tfHoles);
        perfHoles = IntegerParser.getValue(tfPerfHoles);
        measure = controller.getCmbxTimeMeasurement().getValue();

    }

    /**
     * Метод собирает данные с полей плашки на операцию в класс OpData
     * Вызывается при изменении любого значения на операционной плашке
     */
    private void collectOpData(){
        opData.setHoles(holes);
        opData.setPerfHoles(perfHoles);
        opData.setExtraPerimeter(extraPerimeter);
        opData.setStripping(stripping);

        opData.setMechTime(currentNormTime);
    }

    /**
     * Метод устанавливает/восстанавливает начальные значения полей
     * согласно данным в классе OpData
     */
    private void fillOpData(){
        holes = opData.getHoles();
        tfHoles.setText(String.valueOf(holes));

        perfHoles = opData.getPerfHoles();
        tfPerfHoles.setText(String.valueOf(perfHoles));

        extraPerimeter = opData.getExtraPerimeter();
        tfExtraPerimeter.setText(String.valueOf(extraPerimeter));

        stripping = opData.isStripping();
        chbxStripping.setSelected(stripping);

    }

}
