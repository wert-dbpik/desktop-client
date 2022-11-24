package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOperation;
import ru.wert.datapik.chogori.calculator.enums.EPaintingDifficulty;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpPainting extends NormTimes{

    private Integer along;
    private Integer across;
    private EPaintingDifficulty difficulty;
    private Integer holdingTime;


}
