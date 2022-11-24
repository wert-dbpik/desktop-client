package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOperation;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpBending extends NormTimes{

    private Integer bends;
    private Integer men;
    private EBendingTool tool;
}
