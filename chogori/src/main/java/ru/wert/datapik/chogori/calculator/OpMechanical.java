package ru.wert.datapik.chogori.calculator;

import ru.wert.datapik.chogori.calculator.enums.ETimeMeasurement;

import static ru.wert.datapik.chogori.calculator.AbstractOpPlate.*;

public class OpMechanical {

    ETimeMeasurement measure; //требуемые единицы измерения (сек, мин)

    double t;       //толщина материала, мм (пр: 1,5 мм)
    double a;       //длина развертки, мм (пр: 560,8 мм)
    double b;       //ширина развертки, мм (пр: 324,5 мм)

    //Промежуточные итоги
    double p;       //периметр обрабатываемой заготовки, м
    double s;       //площадь обрабатываемой заготовки, м2


    public OpMechanical(double t, int a, int b, ETimeMeasurement measure) {
        this.t = t;
        this.a = a;
        this.b = b;
        this.measure = measure;

        p = 2*(a + b) * MM_TO_M;
        s = a * b * MM2_TO_M2;

    }

    public double countMass(){
        return t * a * b * RO;
    }


    /**
     * Расчет времени на резку
     * @param plusLength дополнительная длина обработки, мм (пр: 250 мм)
     * @param holes количество отверстий (пр: 20 отв)
     * @param perfHoles количество отверстий перфорации (пр: 500 отв)
     * @param useStriping применить зачистку
     */
    public double countCuttingTime(int plusLength, int holes, int perfHoles, boolean useStriping){
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
        if(measure.equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;

        return time;
    }

    /**
     * Расчет времени на гибку
     * @param bends количество гибов (пр: 2 гиба)
     * @param tool тип оборудования (пр: 1 - панелегиб, 2 - универсальный)
     * @param men количество человек, участвующих в гибке (от 1 до 2)
     */
    public double countBendingTime(int bends, int tool, int men){
        final double BENDING_SERVICE_RATIO = 1.25; //коэфффициент, учитывающий 25% времени на обслуживание при гибке
        final double BENDING_SPEED = 0.15; //корость гибки, мин/гиб
        double time;
        time =  bends * BENDING_SPEED * tool * men  //мин
                * BENDING_SERVICE_RATIO;
        if(measure.equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }

    /**
     * Расчет времени на покраску
     * @param c габарит, мм (пр: 600)
     * @param d габарит, мм (пр: 800)
     * @param difficulty сложность детали, мм (пр: I = 1, II = 1,4, III = 2)
     * @param holdingTime время навешивания, зависит от массы детали (5-30 сек)
     */
    public double countPaintingTime(int c, int d, int holdingTime, double difficulty ){
        final int DELTA = 100; //расстояние между деталями

        final double WASHING = 12/60.0; //мойка, мин
        final double WINDING = 6/60.0; //продувка, мин
        final double DRYING = 20/60.0; //сушка, мин
        final double HOLDING_TIME = holdingTime/60.0; //время навешивания, мин

        final int minSize = Math.min(c, d) + DELTA;
        final int maxSize = Math.max(c, d) + DELTA;

        //Количество штанг в сушилке
        int dryingBars;
        if(maxSize < 99) dryingBars = 3;
        else if(maxSize >= 100 && maxSize <= 300) dryingBars = 2;
        else dryingBars = 1;

        int partsOnBar = Math.abs(2500/minSize);

        //Количество штанг в печи
        int bakeBars;
        if(maxSize < 49) bakeBars = 6;
        else if(maxSize >= 50 && maxSize <= 99) bakeBars = 5;
        else if(maxSize >= 100 && maxSize <= 199) bakeBars = 4;
        else if(maxSize >= 200 && maxSize <= 299) bakeBars = 3;
        else if(maxSize >= 300 && maxSize <= 399) bakeBars = 2;
        else bakeBars = 1;

        double time;
        time = HOLDING_TIME //Время навешивания
                + (WASHING + WINDING + DRYING/dryingBars)/partsOnBar //Время подготовки к окрашиванию
                + Math.pow(2*s, 0.7) * difficulty //Время нанесения покрытия
                + 40.0/bakeBars/partsOnBar;  //Время полимеризации

        if(measure.equals(ETimeMeasurement.SEC))
            time = time * MIN_TO_SEC;
        return time;
    }
}
