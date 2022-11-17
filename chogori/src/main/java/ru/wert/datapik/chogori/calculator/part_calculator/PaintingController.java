package ru.wert.datapik.chogori.calculator.part_calculator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;

public class PaintingController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_PAINTING;

    @FXML
    private Label lblNormResult;

    @FXML
    private TextField tfB;

    @FXML
    private ImageView cmbxOnDifficulty;

    @FXML
    private Label lblTimeMeasurement;

    @FXML
    private ImageView ivHelpOnA;

    @FXML
    private ImageView ivHelpOnB;

    @FXML
    private ComboBox<?> cmbxDifficulty;

    @FXML
    private ImageView ivHelpOnHangingTime;

    @FXML
    private TextField tfA;

    @FXML
    private TextField tfHangingTime;

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
