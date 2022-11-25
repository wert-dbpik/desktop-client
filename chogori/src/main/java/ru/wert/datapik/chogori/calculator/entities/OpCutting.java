package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpCutting extends OpData {

    private Integer holes;
    private Integer perfHoles;
    private Integer extraPerimeter;
    private boolean stripping; //Зачистка

}
