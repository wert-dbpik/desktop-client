package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.interfaces.IFormController;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpAssmNut;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateAssmNutsController extends AbstractOpPlate {

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

    private IFormController controller;
    private OpAssmNut opData;

    public OpData getOpData(){
        return opData;
    }

    private int screws; //Количество винтов
    private int vshgs; //Количество комплектов ВШГ
    private int rivets; //Количество заклепок
    private int rivetNuts; //Количество аклепочных гаек
    private int groundSets; //Количество комплектов заземления с этикеткой
    private int others; //Количество другого крепежа

    private ETimeMeasurement measure;

    public void init(IFormController controller, OpAssmNut opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        this.opData = opData;

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new TFColoredInteger(tfScrews, this);
        new TFColoredInteger(tfVSHGs, this);
        new TFColoredInteger(tfRivets, this);
        new TFColoredInteger(tfRivetNuts, this);
        new TFColoredInteger(tfGroundSets, this);
        new TFColoredInteger(tfOthers, this);

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

        final double SCREWS_SPEED = 0.25; //скорость установки вытяжных винтов
        final double VSHGS_SPEED = 0.4; //скорость установки комплектов ВШГ
        final double RIVETS_SPEED = 18 * SEC_TO_MIN; //скорость установки заклепок
        final double RIVET_NUTS_SPEED = 22 * SEC_TO_MIN; //скорость установки заклепочных гаек
        final double GROUND_SETS_SPEED = 1.0; //скорость установки комплекта заземления с этикеткой
        final double OTHERS_SPEED = 15 * SEC_TO_MIN; //скорость установки другого крепежа

        double time;
        time =  screws * SCREWS_SPEED
                + vshgs * VSHGS_SPEED
                + rivets * RIVETS_SPEED
                + rivetNuts * RIVET_NUTS_SPEED
                + groundSets * GROUND_SETS_SPEED
                + others * OTHERS_SPEED;   //мин

        currentNormTime = time;
        collectOpData();
    }


    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        screws = IntegerParser.getValue(tfScrews);
        vshgs = IntegerParser.getValue(tfVSHGs);
        rivets = IntegerParser.getValue(tfRivets);
        rivetNuts = IntegerParser.getValue(tfRivetNuts);
        groundSets = IntegerParser.getValue(tfGroundSets);
        others = IntegerParser.getValue(tfOthers);

        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setScrews(screws);
        opData.setVshgs(vshgs);
        opData.setRivets(rivets);
        opData.setRivetNuts(rivetNuts);
        opData.setGroundSets(groundSets);
        opData.setOthers(others);

        opData.setAssmTime(currentNormTime);
    }

    private void fillOpData(){
        screws = opData.getScrews();
        tfScrews.setText(String.valueOf(screws));

        vshgs = opData.getVshgs();
        tfVSHGs.setText(String.valueOf(vshgs));

        rivets = opData.getRivets();
        tfRivets.setText(String.valueOf(rivets));

        rivetNuts = opData.getRivetNuts();
        tfRivetNuts.setText(String.valueOf(rivetNuts));

        groundSets = opData.getGroundSets();
        tfGroundSets.setText(String.valueOf(groundSets));

        others = opData.getOthers();
        tfOthers.setText(String.valueOf(others));

    }


}
