package ru.wert.tubus.chogori.excel.enums;

/**
 * Цвет показывает входимость деталей и узлов друг в друга
 * Уровень 0 соответствует уровню изделия - верхний уровень
 * Цвет - первые FF показывают на непрозрачность, остальные пары
 * соответствуют RGB, но в шестадцатеричной форме
 */
public enum EColor {

    WHITE   ("00FFFFFF"),
    GREEN   ("FF92D050"),
    BLACK   ("FF000000"),
    ORANGE  ("FFFFC000"),
    YELLOW  ("FFFFFF00"),
    BLUE    ("FF1F497D");

    private final String color;

    EColor( String color) {
        this.color = color;
    }


    public String getColor() {
        return color;
    }

    public static EColor byHEX(String color) {
        for (EColor b : EColor.values()) {
            if (b.color.equalsIgnoreCase(color)) {
                return b;
            }
        }
        return null;
    }

}
