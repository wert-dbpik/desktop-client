package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpCutting extends OpData {

    private Integer holes = 0;
    private Integer perfHoles = 0;
    private Integer extraPerimeter = 0;
    private boolean stripping = false; //Зачистка

    public OpCutting() {
        super.opType = EOpType.CUTTING;
    }
}
