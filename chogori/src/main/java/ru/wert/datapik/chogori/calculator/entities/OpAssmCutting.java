package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.winform.enums.EOperation;

@Getter
@Setter
public class OpAssmCutting extends OpData {

    private Double sealer = 0.0;
    private Double selfAdhSealer = 0.0;
    private Double insulation = 0.0;

    public OpAssmCutting() {
        super.opType = EOpType.ASSM_CUTTINGS;
    }
}
