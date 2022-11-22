package ru.wert.datapik.chogori.calculator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

/**
 * Изначальный расчет нормы времени производится в минутах
 * Конвертация в секунды происходит в методе setTimeMeasurement()
 */
public abstract class AbstractNormsCounter {

    // КОНСТАНТЫ
    public static final double MM_TO_M = 0.001; //перевод мм в метры
    public static final double MM2_TO_M2 = 0.000001; //перевод мм квадратных в квадратные метры
    public static final double MM3_TO_M3 = 0.000000001; //перевод мм квадратных в квадратные метры
    public static final double MIN_TO_SEC = 60; //перевод минут в секунды
    public static final double RO = 0.00000785; //плотность стали кг/м3
    public static final String doubleFormat = "%5.3f";
    public static final String integerFormat = "%10.0f";

    //Переменные
    protected double currentNormTime;


    @FXML
    private TextField tfNormTime;

    @FXML
    private Label lblNormTimeMeasure;


    public AbstractNormsCounter() {
    }

    /**
     * Метод устанавливает расчитанную норму
     */
    public abstract void setNormTime();

    /**
     * Метод возвращает тип нормы времени (МК, покраска и т.д)
     */
    public abstract ENormType getNormType();

    /**
     * Метод расчитывает норму времени в минутах
     */
    public abstract double countNorm();

    /**
     * Метод устанавливает изначальные нулевые значения полей
     */
    public abstract void setZeroValues();

    /**
     * Метод возвращает текущее расчитанное
     */
    public double getCurrentNormTime(){
        return currentNormTime;
    };

    /**
     * Метод устанавливает поле с расчитанной нормой в значением требуемой размерности
     */
    public void setTimeMeasurement(ETimeMeasurement measure){
        double time = currentNormTime;
        String format = doubleFormat;
        lblNormTimeMeasure.setText(measure.getTimeName());
        if (measure.equals(ETimeMeasurement.SEC)) {
            time = currentNormTime * MIN_TO_SEC;
            format = integerFormat;
        }

        tfNormTime.setText(String.format(format,time));
    };
}
