package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOperation;
import ru.wert.datapik.client.entity.models.Material;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpCutting extends NormTimes{

    private Integer holes;
    private Integer perfHoles;
    private Integer extraPerimeter;
    private boolean stripping; //Зачистка

}
