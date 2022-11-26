package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpCutting extends OpData {

    private Integer holes;
    private Integer perfHoles;
    private Integer extraPerimeter;
    private boolean stripping; //Зачистка

    public OpCutting() {
        super.opType = EOpType.CUTTING;
    }
}
