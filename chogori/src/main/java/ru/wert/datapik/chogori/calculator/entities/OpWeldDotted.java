package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpWeldDotted extends OpData {

    private Integer parts = 0; //Количество элементов
    private Integer dots = 0; //Количество точек
    private Integer drops = 0; //Количество прихваток

    public OpWeldDotted() {
        super.normType = ENormType.NORM_MECHANICAL;
        super.opType = EOpType.WELD_DOTTED;
    }
}
