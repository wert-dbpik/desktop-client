package ru.wert.datapik.chogori.calculator.enums;

import lombok.Getter;

public enum EPartBigness {

    SMALL("Мелкая", 5.0),
    BIG("Крупная", 6.0);


    @Getter String toolName;
    @Getter double time;


    EPartBigness(String toolName, double time) {
        this.toolName = toolName;
        this.time = time;
    }
}
