package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
@NoArgsConstructor
public class OpPaint extends OpData {

    private EOpType op = EOpType.PAINTING;
    private Integer along;
    private Integer across;
    private EPaintingDifficulty difficulty;
    private Integer hangingTime;


}
