package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EAssemblingType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
public class OpPaintAssm extends OpData {

    private double area = 0.0;
    private Integer along = 0;
    private Integer across = 0;
    private EAssemblingType assmType = null;

    public OpPaintAssm() {
        super.opType = EOpType.PAINTING_ASSM;
    }
}
