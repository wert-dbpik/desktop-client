package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
@NoArgsConstructor
public class OpCutting extends OpData {

    private EOpType op = EOpType.CUTTING;
    private Integer holes;
    private Integer perfHoles;
    private Integer extraPerimeter;
    private boolean stripping; //Зачистка

}
