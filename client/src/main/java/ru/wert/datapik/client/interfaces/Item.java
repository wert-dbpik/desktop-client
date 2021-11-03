package ru.wert.datapik.client.interfaces;

import java.util.Comparator;

/**
 * Интерфейс содержит основные методы getName(), getId(), toUsefulString()
 * toUsefulString() - имитирует toString() - возвращает строку, которую нужно использовать для вывода
 */
public interface Item {

    String getName();

    Long getId();

    String toUsefulString();


}
