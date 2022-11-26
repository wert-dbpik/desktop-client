package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.chogori.calculator.enums.ESealersWidth;

@Getter
@Setter
@NoArgsConstructor
public class OpLevelingSealer extends OpData {

    private EOpType op = EOpType.LEVELING_SEALER;
    private ESealersWidth sealersWidth; //Ширина уплотнителя
    private Integer paramA; //Размер А
    private Integer paramB;//Размер Б

}
