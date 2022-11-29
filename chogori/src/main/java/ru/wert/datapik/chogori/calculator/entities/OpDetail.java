package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;
import ru.wert.datapik.client.entity.models.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OpDetail extends OpData {

    private String name = null;
    private Integer quantity = 1;
    private Material material = null;
    private Integer paramA = 0;
    private Integer paramB = 0;
    private List<OpData> operations = new ArrayList<>();

    public OpDetail() {
        super.normType = ENormType.NORM_DETAIL;
        super.opType = EOpType.DETAIL;
    }
}
