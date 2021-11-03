package ru.wert.datapik.utils.search;

import ru.wert.datapik.client.interfaces.Item;

import java.util.List;

public interface Searchable<P extends Item> {

    void setSearchedText(String searchedText);

    String getSearchedText();

    void updateView();

    /**
     * Метод вызывается для обновления таблицы
     */
    void updateSearchedView();

    /**
     * Метод возвращает текущий список таблицы, с этим списком работает поиск
     * Метод необходим для ускорения работы поиска и вообще апдейта таблиц
     */
    List<P> getCurrentItemList();

    /**
     * Метод устанавливает текущий список элементов, отображаемых в таблице
     * Он вызывается, например в Тасках для передачи окончательного списка
     */
    void setCurrentItemSearchedList(List<P> currentItemList);


}
