package ru.wert.datapik.chogori.calculator.enums;

import lombok.Getter;

public enum EBendingTool {

    UNIVERSAL("Универсал", 2),
    PANELEGIB("Панелегиб", 1);


    @Getter String toolName;
    @Getter Integer toolRatio;

    EBendingTool(String toolName, Integer toolRatio) {
        this.toolName = toolName;
        this.toolRatio = toolRatio;
    }
}
