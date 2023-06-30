package ru.wert.tubus.chogori.common.interfaces;

import java.util.List;

/**
 * Интерфейс содержит метод для сортировки выводимого списка Чертежей соответствующим компаратором
 * @param <P>
 */
public interface Sorting<P> {
    /**
     * Метод сортирует предложенный лист
     * @param list List<P>
     */
    void sortItemList(List<P> list);
}
