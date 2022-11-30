package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.ENormType;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

/**
 * Класс наследуется всеми entities
 */
@Getter
@Setter
public class OpData {

    protected ENormType normType; //Тип нормы по участкам (МК, ППК и т.д.)
    protected EOpType opType; //Тип олперации (Гибка, покраска и т.д))

    private double mechTime; //МК
    private double paintTime; //ППК
    private double assmTime; //Сборка
    private double packTime; //Упаковка

    private double totalTime; //Общее время


}
