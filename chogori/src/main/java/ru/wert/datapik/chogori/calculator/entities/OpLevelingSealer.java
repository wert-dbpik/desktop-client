package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.ESealersWidth;

@Getter
@Setter
public class OpLevelingSealer extends OpData {

    private ESealersWidth sealersWidth = ESealersWidth.W10; //Ширина уплотнителя
    private Integer paramA = 0; //Размер А
    private Integer paramB = 0;//Размер Б

    public OpLevelingSealer() {
        super.normType = ENormType.NORM_ASSEMBLING;
        super.opType = EOpType.LEVELING_SEALER;
    }
}
