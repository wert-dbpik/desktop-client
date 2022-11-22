package ru.wert.datapik.chogori.calculator.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;
import ru.wert.datapik.chogori.calculator.components.TFColoredInteger;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

public class LocksmithController extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfNumOfCountersinkings;

    @FXML
    private TextField tfNumOfThreadings;

    @FXML
    private TextField tfNumOfRivets;

    @FXML
    private TextField tfNumOfSmallSawings;

    @FXML
    private TextField tfNumOfBigSawings;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private Label lblOperationName;

    private PartCalculatorController controller;

    private int rivets; //Количество заклепок
    private int countersinkings; //количество зенкуемых отверстий
    private int threadings; //Количество нарезаемых резьб
    private int smallSawings; //Количество резов на малой пиле
    private int bigSawings; //Количество резов на большой пиле
    private ETimeMeasurement measure;

    public void init(PartCalculatorController controller){
        this.controller = controller;
        controller.getAddedOperations().add(this);
        setZeroValues();
        setNormTime();

        new TFColoredInteger(tfNumOfRivets, this);
        new TFColoredInteger(tfNumOfCountersinkings, this);
        new TFColoredInteger(tfNumOfThreadings, this);
        new TFColoredInteger(tfNumOfSmallSawings, this);
        new TFColoredInteger(tfNumOfBigSawings, this);

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

        boolean res = countInitialValues();
        if(!res) return 0.0;

        final double RIVETS_SPEED = 18 * SEC_TO_MIN; //скорость онденсаторной сварки точкой, мин/элемент
        final double COUNTERSINKING_SPEED = 0.31; //скорость контактной сварки, мин/точку
        final double THREADING_SPEED = 0.37; //скорость сварки прихватками, мин/прихватку
        final double SMALL_SAWING_SPEED = 0.2; //скорость сварки прихватками, мин/прихватку
        final double BIG_SAWING_SPEED = 1.0; //скорость сварки прихватками, мин/прихватку

        double time;
        time =  rivets * RIVETS_SPEED
                + countersinkings * COUNTERSINKING_SPEED
                + threadings * THREADING_SPEED
                + smallSawings * SMALL_SAWING_SPEED
                + bigSawings * BIG_SAWING_SPEED;   //мин

        currentNormTime = time;
        return time;
    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override
    public void setZeroValues() {
        tfNumOfRivets.setText("0");
        tfNumOfCountersinkings.setText("0");
        tfNumOfThreadings.setText("0");
        tfNumOfSmallSawings.setText("0");
        tfNumOfBigSawings.setText("0");
        setTimeMeasurement(controller.getCmbxTimeMeasurement().getValue());
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {
        try {
            rivets = Integer.parseInt(tfNumOfRivets.getText().trim());
            countersinkings = Integer.parseInt(tfNumOfCountersinkings.getText().trim());
            threadings = Integer.parseInt(tfNumOfThreadings.getText().trim());
            smallSawings = Integer.parseInt(tfNumOfSmallSawings.getText().trim());
            bigSawings = Integer.parseInt(tfNumOfBigSawings.getText().trim());
            measure = controller.getCmbxTimeMeasurement().getValue();
        } catch (NumberFormatException e) {
            tfNormTime.setText("");
        }
        return true;
    }


}
