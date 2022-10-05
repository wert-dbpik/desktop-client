package ru.wert.datapik.chogori.entities.drafts.commands;

import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.chogori.entities.drafts.Draft_TableView;

/**
 * Класс дублирует функцию AddCommand, но позволяет вернуть сохраненный Чертеж,
 * чтобы далее использовать его id для добавления в список (File, Long)
 */
public class Draft_MultipleAddCommand extends Draft_AddCommand{
    /**
     * Конструктор
     * @param newItem Draft
     * @param tableView Draft_TableView
     */
    public Draft_MultipleAddCommand(Draft newItem, Draft_TableView tableView) {
        super(newItem, tableView);
    }

    public Draft addDraft(){
        super.execute();
        return super.getResult();
    }
}
