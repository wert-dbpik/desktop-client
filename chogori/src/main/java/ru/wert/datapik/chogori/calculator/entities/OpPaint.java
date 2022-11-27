package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
public class OpPaint extends OpData {

    private Integer along = 0;
    private Integer across = 0;
    private EPaintingDifficulty difficulty;
    private Integer hangingTime = 20;

    public OpPaint() {
        super.opType = EOpType.PAINTING;
    }
}
