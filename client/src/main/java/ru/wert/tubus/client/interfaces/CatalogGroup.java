package ru.wert.tubus.client.interfaces;

/**
 * Интерфейс представляет расширение классу Item
 * Если Item, это по-простому ЭЛЕМЕНТ, то CatalogGroup - ЭЛЕМЕНТ В ДРЕВОВИДНОЙ СТРУКТУРЕ,
 * то есть элемент, участвующий в построении дерева
 * Интерфейс необходим для описания древовидных структур, т.н. каталогов
 */
public interface CatalogGroup extends Item {

    Long getParentId();

    void setParentId(Long parentId);

}
