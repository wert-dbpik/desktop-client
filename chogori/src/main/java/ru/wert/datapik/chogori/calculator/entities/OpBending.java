package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpBending extends OpData {

    private Integer bends = 1;
    private Integer men = 1;
    private EBendingTool tool;

    public OpBending() {
        super.normType = ENormType.NORM_MECHANICAL;
        super.opType = EOpType.BENDING;
    }
}
