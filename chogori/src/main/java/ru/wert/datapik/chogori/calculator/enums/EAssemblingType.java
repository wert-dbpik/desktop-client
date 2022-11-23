package ru.wert.datapik.chogori.calculator.enums;

import lombok.Getter;

public enum EAssemblingType {

    SOLID("Глухой шкаф", 1.686),
    FRAME("Рама (кросс)", 2.4);


    @Getter String type;
    @Getter Double speed;

    EAssemblingType(String type, Double speed) {
        this.type = type;
        this.speed = speed;
    }
}
