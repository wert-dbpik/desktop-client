package ru.wert.datapik.chogori.calculator;

/**
 * Интерфейс верхнего уровня, описывает все операции, добавленные в лист операций
 *
 */
public interface IOperation {

    /**
     * Метод возвращает тип нормы времени (МК, покраска и т.д)
     */
    public abstract ENormType getNormType();


}
