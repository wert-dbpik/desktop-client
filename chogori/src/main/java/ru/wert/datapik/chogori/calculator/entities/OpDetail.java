package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOpPlate;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.client.entity.models.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OpDetail extends OpData {

    private String name = null;
    private Material material = null;
    private Integer paramA = 0;
    private Integer paramB = 0;
    private List<OpData> operations = new ArrayList<>();

    public OpDetail() {
        super.opType = EOpType.DETAIL;
    }
}
