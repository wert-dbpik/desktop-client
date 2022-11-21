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
import ru.wert.datapik.chogori.calculator.components.BXBendingTool;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

public class BendingController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfNumOfBendings;

    @FXML
    private TextField tfNumOfMen;

    @FXML
    private ComboBox<EBendingTool> cmbxBendingTool;

    @FXML
    private TextField tfNormTime;

    private PartCalculatorController controller;

    private int bends;
    private int men;
    private double toolRatio;
    private ETimeMeasurement measure;

    public void init(PartCalculatorController controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        new BXBendingTool().create(cmbxBendingTool);
        setZeroValues();
        setNormTime();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        tfNumOfBendings.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfNumOfMen.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        cmbxBendingTool.valueProperty().addListener((observable, oldValue, newValue) -> {
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
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
        controller.countSumNormTimeByShops();
    }

    @Override//AbstractNormsCounter
    public double countNorm(){

        boolean res = countInitialValues();
        if(!res) return 0.0;

        final double BENDING_SERVICE_RATIO = 1.25; //коэфффициент, учитывающий 25% времени на обслуживание при гибке
        final double BENDING_SPEED = 0.15; //корость гибки, мин/гиб
        double time;
        time =  bends * BENDING_SPEED * toolRatio * men  //мин
                * BENDING_SERVICE_RATIO;

        currentNormTime = time;
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfNumOfBendings.setText("1");
        tfNumOfMen.setText("1");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {
        try {
            bends = Integer.parseInt(tfNumOfBendings.getText().trim());
            men = Integer.parseInt(tfNumOfMen.getText().trim());
            toolRatio = cmbxBendingTool.getValue().getToolRatio();
            measure = controller.getCmbxTimeMeasurement().getValue();
        } catch (NumberFormatException e) {
            tfNormTime.setText("");
        }
        return true;
    }


}
