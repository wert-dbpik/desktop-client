package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
public class OpAssmNode extends OpData {

    private Integer postLocks; //почтовые замки
    private Integer doubleLocks; //замки с рычагами
    private Integer mirrors; //стекла в дверь
    private Integer detectors; //извещатели
    private Integer connectionBoxes;//коробки соединительные типа КС-4

    public OpAssmNode() {
        super.opType = EOpType.ASSM_NODES;
    }
}
