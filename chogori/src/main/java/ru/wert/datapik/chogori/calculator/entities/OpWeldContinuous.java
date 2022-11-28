package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;

@Getter
@Setter
public class OpWeldContinuous extends OpData {

    private Integer seamLength = 0; //Длина шва
    private EPartBigness partBigness = null; //размер собираемых деталей
    private Integer men = 1; //Число человек, работающих над операцией
    private boolean stripping = false; //Использовать зачистку
    private boolean preEnterSeams = true; //количество швов вводить вручную
    private Integer seams = 0; //Количество швов заданное пользователем
    private Integer connectionLength = 0; //Длина сединения на которую расчитывается количество точек
    private Integer step = 0; //шаг точек

    public OpWeldContinuous() {
        super.opType = EOpType.WELD_CONTINUOUS;
    }
}
