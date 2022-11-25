package ru.wert.datapik.chogori.calculator;

import ru.wert.datapik.chogori.calculator.entities.OpData;

/**
 * Интерфейс верхнего уровня, описывает все операции, добавленные в лист операций
 *
 */
public interface IOpPlate {

    /**
     * Метод возвращает тип нормы времени (МК, покраска и т.д)
     */
    ENormType getNormType();

    /**
     * Метод возвращает данные по операции, в том числе и нормы времени по участкам
     * Используется для восстановления закрытых плашек и сохранения расчетов
     */
    OpData getOpData();


}
