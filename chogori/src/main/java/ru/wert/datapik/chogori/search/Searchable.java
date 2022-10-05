package ru.wert.datapik.chogori.search;

import ru.wert.datapik.client.interfaces.Item;

import java.util.List;

public interface Searchable<P extends Item> {
    /**
     * Searchable
     */
    void setSearchedText(String searchedText);
    /**
     * Searchable
     */
    String getSearchedText();
    /**
     * Searchable
     */
    void updateView();

    /**
     * Searchable
     * Метод вызывается для обновления таблицы
     */
    void updateSearchedView();

    /**
     * Searchable
     * Метод возвращает текущий список таблицы, с этим списком работает поиск
     * Метод необходим для ускорения работы поиска и вообще апдейта таблиц
     */
    List<P> getCurrentItemSearchedList();

    /**
     * Searchable
     * Метод устанавливает текущий список элементов, отображаемых в таблице
     * Он вызывается, например в Тасках для передачи окончательного списка
     */
    void setCurrentItemSearchedList(List<P> currentItemList);


}
