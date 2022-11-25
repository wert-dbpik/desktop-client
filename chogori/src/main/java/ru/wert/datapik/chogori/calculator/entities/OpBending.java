package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EBendingTool;

@Getter
@Setter
@NoArgsConstructor
public class OpBending extends OpData {

    private Integer bends;
    private Integer men;
    private EBendingTool tool;
}
