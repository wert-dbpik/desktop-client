package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpWeldingDotted extends OpData {

    private int parts; //Количество элементов
    private int dots; //Количество точек
    private int drops; //Количество прихваток
}
