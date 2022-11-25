package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpAssmNut extends OpData {

    private Integer screws; //винты
    private Integer vshgs; //ВШГ - винт-шайба-гайка
    private Integer rivets; //заклепки
    private Integer rivetNuts; //заклепочные гайки
    private Integer groundSets;//заземление(гайка-шайба-этикетка)
    private Integer others;//другой крепеж

}
