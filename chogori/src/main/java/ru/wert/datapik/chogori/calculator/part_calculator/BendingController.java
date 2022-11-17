package ru.wert.datapik.chogori.calculator.part_calculator;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;

public class BendingController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblNormResult;

    @FXML
    private TextField tfNumOfMen;

    @FXML
    private Label lblTimeMeasurement;

    @FXML
    private TextField tfNumOfBendings;

    @FXML
    private ComboBox<?> cmbxEquipment;

    private PartCalculatorController controller;

    public void init(PartCalculatorController controller){
        this.controller = controller;
    }

    /**
     * Метод устанавливает расчитанную норму
     */
    @Override
    public void setNormTime() {

    }

    @Override//AbstractNormsCounter
    public double countNorm(){
        return 0.0;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {

    }


}
