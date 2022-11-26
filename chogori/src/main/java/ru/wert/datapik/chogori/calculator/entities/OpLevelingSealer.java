package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.ESealersWidth;

@Getter
@Setter
public class OpLevelingSealer extends OpData {

    private ESealersWidth sealersWidth; //Ширина уплотнителя
    private Integer paramA; //Размер А
    private Integer paramB;//Размер Б

    public OpLevelingSealer() {
        super.opType = EOpType.LEVELING_SEALER;
    }
}
