package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

@Getter
@Setter
@NoArgsConstructor
public class OpPaintingAssm  extends OpData {

    private double area;
    private Integer along;
    private Integer across;
    private EPaintingDifficulty difficulty;
    private Integer holdingTime;


}
