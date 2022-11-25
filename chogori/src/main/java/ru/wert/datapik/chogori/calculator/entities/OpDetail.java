package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOpPlate;
import ru.wert.datapik.client.entity.models.Material;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpDetail extends OpData {

    private String name;
    private Material material;
    private Integer paramA;
    private Integer paramB;
    private List<IOpPlate> operations;

}
