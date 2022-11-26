package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpWeldDotted extends OpData {

    private EOpType op = EOpType.WELD_DOTTED;
    private int parts; //Количество элементов
    private int dots; //Количество точек
    private int drops; //Количество прихваток

    public OpWeldDotted() {
        super.opType = EOpType.WELD_DOTTED;
    }
}
