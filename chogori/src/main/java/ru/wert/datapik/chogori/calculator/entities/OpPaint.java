package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
public class OpPaint extends OpData {

    private Integer along;
    private Integer across;
    private EPaintingDifficulty difficulty;
    private Integer hangingTime;

    public OpPaint() {
        super.opType = EOpType.PAINTING;
    }
}
