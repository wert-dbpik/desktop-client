package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPartBigness;

@Getter
@Setter
@NoArgsConstructor
public class OpWeldContinuous extends OpData {

    private EOpType op = EOpType.WELD_CONTINUOUS;
    private Integer seamLength; //Длина шва
    private EPartBigness partBigness; //азмер собираемых деталей
    private Integer men; //Число человек, работающих над операцией
    private boolean stripping; //Использовать зачистку
    private boolean preEnterSeams; //количество швов вводить вручную
    private Integer seams; //Количество швов заданное пользователем
    private Integer connectionLength; //Длина сединения на которую расчитывается количество точек
    private Integer step; //шаг точек

}
