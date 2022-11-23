package ru.wert.datapik.chogori.calculator.enums;

import lombok.Getter;

public enum ESealersWidth {

    W8("8", 0.01, 0.003),
    W10("10", 0.011, 0.003),
    W12("12", 0.016, 0.004),
    W14("14", 0.024, 0.006),
    W16("16", 0.035, 0.009);



    @Getter String width;
    @Getter Double compA;
    @Getter Double compB;

    ESealersWidth(String width, Double compA, Double compB) {
        this.width = width;
        this.compA = compA;
        this.compB = compB;
    }
}
