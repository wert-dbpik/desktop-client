package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpAssmNode extends OpData {

    private Integer postLocks = 0; //почтовые замки
    private Integer doubleLocks = 0; //замки с рычагами
    private Integer mirrors = 0; //стекла в дверь
    private Integer detectors = 0; //извещатели
    private Integer connectionBoxes = 0;//коробки соединительные типа КС-4

    public OpAssmNode() {
        super.opType = EOpType.ASSM_NODES;
    }
}
