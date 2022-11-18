package ru.wert.datapik.chogori.calculator.part_calculator;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.AbstractNormsCounter;
import ru.wert.datapik.chogori.calculator.ENormType;

public class CuttingController  extends AbstractNormsCounter {

    @Getter
    private ENormType normType = ENormType.NORM_MECHANICAL;

    @FXML
    private Label lblOperationName;

    @FXML
    private ImageView ivDeleteOperation;

    @FXML
    private TextField tfNumOfHoles;

    @FXML
    private TextField tfNormTime;

    @FXML
    private TextField tfNumOfPerfHoles;

    @FXML
    private CheckBox chbxUseStripping;

    @FXML
    private TextField tfExtraPerimeter;

    @FXML
    private ImageView ivHelpOnNumOfHoles;

    @FXML
    private ImageView ivHelpOnExtraPerimeter;

    @FXML
    private ImageView ivHelpOnUseStripping;

    @FXML
    private ImageView ivHelpOnNumOfPerfHoles;

    private PartCalculatorController controller;
    private double p; //Периметр контура развертки
    private double s; //Площадь развертки
    private double plusLength; //Дополнительный периметр обработки
    private double t; //Толщина материала
    private double paramA; //Параметр А развертки
    private double paramB; //Параметр B развертки
    private boolean useStriping = false; //Применить зачистку
    private int holes; //Количество отверстий в развертке
    private int perfHoles; //Количество перфораций в развертке
    private ETimeMeasurement measure; //Ед. измерения нормы времени


    public void init(PartCalculatorController controller){
        this.controller = controller;
        setZeroValues();
        setNormTime();

        lblOperationName.setStyle("-fx-text-fill: saddlebrown");

        tfNumOfHoles.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfNumOfPerfHoles.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        tfExtraPerimeter.textProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        chbxUseStripping.selectedProperty().addListener((observable, oldValue, newValue) -> {
            setNormTime();
        });

        ivDeleteOperation.setOnMouseClicked(e->{
            controller.getAddedOperations().remove(this);
            VBox box = controller.getListViewTechOperations().getSelectionModel().getSelectedItem();
            controller.getListViewTechOperations().getItems().remove(box);
            currentNormTime = 0.0;
            controller.countTotalNormTime();
        });

    }

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    @Override//AbstractNormsCounter
    public void setZeroValues(){
        tfNumOfHoles.setText("0");
        tfNumOfPerfHoles.setText("0");
        tfExtraPerimeter.setText("0");
        chbxUseStripping.setSelected(true);
    }

    /**
     * Метод устанавливает расчитанную норму
     */
    @Override
    public void setNormTime() {
        tfNormTime.setText(String.valueOf(countNorm()));
        controller.countTotalNormTime();
    }

    @Override//AbstractNormsCounter
    public double countNorm(){

        boolean res = countInitialValues();
        if(!res) return 0.0;

        final double REVOLVER_SPEED = 0.057; //скорость вырубки одного элемента револьвером, мин/уд
        final double PERFORATION_SPEED = 0.007; //корость перфорирования, мин/уд
        final double CUTTING_SERVICE_RATIO = 1.22; //коэфффициент, учитывающий 22% времени на обслуживание при резке
        final double PLUS_LENGTH = plusLength * MM_TO_M;
        double speed;
        //Скорость резания, м/мин
        if (t < 1.5) speed = 5.5;
        else if (t >= 1.5 && t < 2) speed = 5.0;
        else if (t >= 2 && t < 2.5 ) speed = 4.0;
        else if (t >= 2.5 && t < 3.0) speed = 3.0;
        else speed = 1.9;

        //Время зачистки
        double strippingTime; //мин
        if(useStriping){
            strippingTime = ((p + PLUS_LENGTH) * 2.5 + holes) / 60;
        } else
            strippingTime = 0.0;

        double time;

        time = ((p + PLUS_LENGTH)/speed                 //Время на резку по периметру
                + 1.28 * s                              //Время подготовительное - заключительоне
                + REVOLVER_SPEED * holes                //Время на пробивку отверстий
                + PERFORATION_SPEED * perfHoles)        //Время на пробивку перфорации
                * CUTTING_SERVICE_RATIO
                + strippingTime;


        currentNormTime = time;//результат в минутах
        return time;
    }

    /**
     * Устанавливает и расчитывает значения, заданные пользователем
     */
    private boolean countInitialValues() {
        try {
            paramA = Double.parseDouble(controller.getTfA().getText().trim());
            paramB = Double.parseDouble(controller.getTfB().getText().trim());
            t = controller.getCmbxMaterial().getValue().getParamS();
            if (paramA == 0.0 || paramB == 0.0 || t == 0) return false;

            p = 2*(paramA + paramB) * MM_TO_M;
            s = paramA * paramB * MM2_TO_M2;
            plusLength = Double.parseDouble(tfExtraPerimeter.getText().trim());
            useStriping = chbxUseStripping.isSelected();
            holes = Integer.parseInt(tfNumOfHoles.getText().trim());
            perfHoles = Integer.parseInt(tfNumOfPerfHoles.getText().trim());
            measure = controller.getCmbxTimeMeasurement().getValue();
        } catch (NumberFormatException e) {
            tfNormTime.setText("");
        }
        return true;
    }

}
