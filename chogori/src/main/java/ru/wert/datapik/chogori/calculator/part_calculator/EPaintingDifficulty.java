package ru.wert.datapik.chogori.calculator.part_calculator;

import lombok.Getter;

public enum EPaintingDifficulty {

    SIMPLE("Простой", 1.0),
    MIDDLE("Средний", 1.4),
    MOST("Сложный", 2.0);


    @Getter String difficultyName;
    @Getter double difficultyRatio;

    EPaintingDifficulty(String difficultyName, double difficultyRatio) {
        this.difficultyName = difficultyName;
        this.difficultyRatio = difficultyRatio;
    }
}
