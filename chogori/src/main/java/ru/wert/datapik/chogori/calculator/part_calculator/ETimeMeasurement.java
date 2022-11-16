package ru.wert.datapik.chogori.calculator.part_calculator;

import lombok.Getter;

public enum ETimeMeasurement {

    SEC("сек"),
    MIN("мин");

    @Getter String timeName;

    ETimeMeasurement(String timeName) {
        this.timeName = timeName;
    }
}
