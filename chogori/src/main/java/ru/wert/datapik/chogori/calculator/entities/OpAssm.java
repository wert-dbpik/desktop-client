package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOpPlate;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

import java.util.List;

@Getter
@Setter
public class OpAssm extends OpData {

    private String name;
    private List<IOpPlate> operations;

    public OpAssm() {
        super.opType = EOpType.ASSM;
    }
}
