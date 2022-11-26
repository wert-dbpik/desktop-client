package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.AbstractOpPlate;
import ru.wert.datapik.chogori.calculator.IFormMenu;
import ru.wert.datapik.chogori.calculator.components.BXBendingTool;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.entities.OpBending;
import ru.wert.datapik.chogori.calculator.entities.OpData;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;
import ru.wert.datapik.chogori.calculator.utils.IntegerParser;

public class PlateBendController extends AbstractOpPlate {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfBends;

    @FXML
    private TextField tfMen;

    @FXML
    private ComboBox<EBendingTool> cmbxBendingTool;

    @FXML
    private TextField tfNormTime;

    private IFormMenu controller;
    private OpBending opData;

    public OpData getOpData(){
        return opData;
    }

    private int bends;
    private int men;
    private double toolRatio;
    private ETimeMeasurement measure;

    public void init(IFormMenu controller, OpBending opData){
        this.controller = controller;
        controller.getAddedPlates().add(this);
        if(opData == null){
            this.opData = new OpBending();
            setZeroValues();
        } else {
            this.opData = opData;
            fillOpData();
        }

        new BXBendingTool().create(cmbxBendingTool);
        new TFColoredInteger(tfBends, this);
        new TFColoredInteger(tfMen, this);
        setZeroValues();
        setNormTime();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        cmbxBendingTool.valueProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedPlates().remove(this);
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
    public void countNorm(){

        countInitialValues();

        final double BENDING_SERVICE_RATIO = 1.25; //коэфффициент, учитывающий 25% времени на обслуживание при гибке
        final double BENDING_SPEED = 0.15; //корость гибки, мин/гиб
        double time;
        time =  bends * BENDING_SPEED * toolRatio * men  //мин
                * BENDING_SERVICE_RATIO;

        currentNormTime = time;
        collectOpData();
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfBends.setText("1");
        tfMen.setText("1");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private void countInitialValues() {

        bends = IntegerParser.getValue(tfBends);
        men = IntegerParser.getValue(tfMen);
        toolRatio = cmbxBendingTool.getValue().getToolRatio();
        measure = controller.getCmbxTimeMeasurement().getValue();
    }

    private void collectOpData(){
        opData.setBends(bends);
        opData.setMen(men);
        opData.setTool(cmbxBendingTool.getValue());

        opData.setMechTime(currentNormTime);
    }

    private void fillOpData(){
        tfBends.setText(String.valueOf(opData.getBends()));
        tfMen.setText(String.valueOf(opData.getMen()));
        cmbxBendingTool.setValue(opData.getTool());

    }


}
