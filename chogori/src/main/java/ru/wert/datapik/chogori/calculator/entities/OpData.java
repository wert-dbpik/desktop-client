package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

/**
 * Класс наследуется всеми entities
 */
@Getter
@Setter
public class OpData {

    protected
    protected EOpType opType;

    private double mechTime = 0.0; //МК
    private double paintTime = 0.0; //ППК
    private double assmTime = 0.0; //Сборка
    private double packTime = 0.0; //Упаковка

    private double totalTime = 0.0; //Общее время


}
