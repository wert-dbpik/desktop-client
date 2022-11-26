package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.winform.enums.EOperation;

@Getter
@Setter
@NoArgsConstructor
public class OpAssmCutting extends OpData {

    private EOpType op = EOpType.ASSM_CUTTINGS;
    private Double sealer;
    private Double selfAdhSealer;
    private Double insulation;
}
