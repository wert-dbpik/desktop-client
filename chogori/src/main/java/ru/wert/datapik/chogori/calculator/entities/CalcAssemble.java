package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.AbstractOperationCounter;
import ru.wert.datapik.chogori.calculator.IOperation;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CalcAssemble extends NormTimes{

    private String name;
    private List<IOperation> operations;

}
