package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.concurrent.Service;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;

import java.util.List;

@Slf4j
public class Draft_DeleteCommand implements ICommand {

    private final List<Draft> items;
    private final Draft_TableView tableView;

    /**
     *
     * @param tableView Draft_TableView
     */
    public Draft_DeleteCommand(List<Draft> items, Draft_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        Service<Void> deleteDrafts = new ServiceDeleteDrafts(items, tableView);
        deleteDrafts.restart();
    }
}
