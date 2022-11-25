package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EAssemblingType;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
@NoArgsConstructor
public class OpPaintAssm extends OpData {

    private double area;
    private Integer along;
    private Integer across;
    private EAssemblingType assmType;
}
