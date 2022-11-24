package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.IMenuCalculator;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class AssmNutsController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_ASSEMBLING;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfOthers;

    @FXML
    private TextField tfGroundSets;

    @FXML
    private TextField tfVSHGs;

    @FXML
    private TextField tfRivets;

    @FXML
    private Label lblNormTimeMeasure;

    @FXML
    private TextField tfRivetNuts;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private Label lblOperationName;

    @FXML
    private TextField tfScrews;

    private IMenuCalculator controller;

    private int screws; //Количество винтов
    private int VSHGs; //Количество комплектов ВШГ
    private int rivets; //Количество заклепок
    private int rivetNuts; //Количество аклепочных гаек
    private int groundSets; //Количество комплектов заземления с этикеткой
    private int others; //Количество другого крепежа

    private ETimeMeasurement measure;

    public void init(IMenuCalculator controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        setZeroValues();
        setNormTime();

        new TFColoredInteger(tfScrews, this);
        new TFColoredInteger(tfVSHGs, this);
        new TFColoredInteger(tfRivets, this);
        new TFColoredInteger(tfRivetNuts, this);
        new TFColoredInteger(tfGroundSets, this);
        new TFColoredInteger(tfOthers, this);


        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

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

        final double SCREWS_SPEED = 0.25; //скорость установки вытяжных винтов
        final double VSHGS_SPEED = 0.4; //скорость установки комплектов ВШГ
        final double RIVETS_SPEED = 18 * SEC_TO_MIN; //скорость установки заклепок
        final double RIVET_NUTS_SPEED = 22 * SEC_TO_MIN; //скорость установки заклепочных гаек
        final double GROUND_SETS_SPEED = 1.0; //скорость установки комплекта заземления с этикеткой
        final double OTHERS_SPEED = 15 * SEC_TO_MIN; //скорость установки другого крепежа

        double time;
        time =  screws * SCREWS_SPEED
                + VSHGs * VSHGS_SPEED
                + rivets * RIVETS_SPEED
                + rivetNuts * RIVET_NUTS_SPEED
                + groundSets * GROUND_SETS_SPEED
                + others * OTHERS_SPEED;   //мин

        currentNormTime = time;
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfScrews.setText("0");
        tfVSHGs.setText("0");
        tfRivets.setText("0");
        tfRivetNuts.setText("0");
        tfGroundSets.setText("0");
        tfOthers.setText("0");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        screws = IntegerParser.getValue(tfScrews);
        VSHGs = IntegerParser.getValue(tfVSHGs);
        rivets = IntegerParser.getValue(tfRivets);
        rivetNuts = IntegerParser.getValue(tfRivetNuts);
        groundSets = IntegerParser.getValue(tfGroundSets);
        others = IntegerParser.getValue(tfOthers);

        measure = controller.getCmbxTimeMeasurement().getValue();
    }


}
