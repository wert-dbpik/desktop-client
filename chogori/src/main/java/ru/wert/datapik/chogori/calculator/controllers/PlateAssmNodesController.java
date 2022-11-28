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
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpAssmNode;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateAssmNodesController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_ASSEMBLING;

    @FXML
    private TextField tfMirrors;

    @FXML
    private TextField tfDoubleLocks;

    @FXML
    private TextField tfConnectionBoxes;

    @FXML
    private TextField tfPostLocks;

    @FXML
    private TextField tfDetectors;

    @FXML
    private TextField tfNormTime;

    @FXML
    private Label lblNormTimeMeasure;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private Label lblOperationName;

    private IFormMenu controller;
    private OpAssmNode opData;

    public OpData getOpData(){
        return opData;
    }

    private int postLocks; //Количество почтовых замков
    private int doubleLocks; //Количество замков с рычагами
    private int mirrors; //Количество стекол
    private int detectors; //Количество извещателей ИО-102
    private int connectionBoxes; //Количество еоробок соединительных КС-4

    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpAssmNode opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        this.opData = opData;

        fillOpData(); //Должен стоять до навешивагия слушателей на TextField

        new TFColoredInteger(tfPostLocks, this);
        new TFColoredInteger(tfDoubleLocks, this);
        new TFColoredInteger(tfMirrors, this);
        new TFColoredInteger(tfDetectors, this);
        new TFColoredInteger(tfConnectionBoxes, this);

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

        final double POST_LOCKS_SPEED = 0.25; //скорость установки вытяжных винтов
        final double DOUBLE_LOCKS_SPEED = 0.4; //скорость установки комплектов ВШГ
        final double MIRRORS_SPEED = 18 * SEC_TO_MIN; //скорость установки заклепок
        final double DETECTORS_SPEED = 22 * SEC_TO_MIN; //скорость установки заклепочных гаек
        final double CONNECTION_BOXES_SPEED = 1.0; //скорость установки комплекта заземления с этикеткой

        double time;
        time =  postLocks * POST_LOCKS_SPEED
                + doubleLocks * DOUBLE_LOCKS_SPEED
                + mirrors * MIRRORS_SPEED
                + detectors * DETECTORS_SPEED
                + connectionBoxes * CONNECTION_BOXES_SPEED;   //мин

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
//    @Override
//    public void setZeroValues() {
//        tfPostLocks.setText("0");
//        tfDoubleLocks.setText("0");
//        tfMirrors.setText("0");
//        tfDetectors.setText("0");
//        tfConnectionBoxes.setText("0");
//
//        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
//    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        postLocks = IntegerParser.getValue(tfPostLocks);
        doubleLocks = IntegerParser.getValue(tfDoubleLocks);
        mirrors = IntegerParser.getValue(tfMirrors);
        detectors = IntegerParser.getValue(tfDetectors);
        connectionBoxes = IntegerParser.getValue(tfConnectionBoxes);

        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setPostLocks(postLocks);
        opData.setDoubleLocks(doubleLocks);
        opData.setMirrors(mirrors);
        opData.setDetectors(detectors);
        opData.setConnectionBoxes(connectionBoxes);

        opData.setAssmTime(currentNormTime);
    }

    private void fillOpData(){
        postLocks = opData.getPostLocks();
        tfPostLocks.setText(String.valueOf(postLocks));

        doubleLocks = opData.getDoubleLocks();
        tfDoubleLocks.setText(String.valueOf(doubleLocks));

        mirrors = opData.getMirrors();
        tfMirrors.setText(String.valueOf(mirrors));

        detectors = opData.getDetectors();
        tfDetectors.setText(String.valueOf(detectors));

        connectionBoxes = opData.getConnectionBoxes();
        tfConnectionBoxes.setText(String.valueOf(connectionBoxes));

    }

}
