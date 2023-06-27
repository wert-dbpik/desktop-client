package ru.wert.datapik.chogori.entities.prefixes.commands;

import javafx.event.Event;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;

import ru.wert.datapik.chogori.entities.prefixes.Prefix_TableView;
import ru.wert.datapik.client.entity.models.Prefix;

import java.util.List;

public class _PrefixCommands implements ItemCommands<Prefix> {

    private final Prefix_TableView tableView;
    private String accWindowRes;

    public _PrefixCommands(Prefix_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Prefix newItem){
        ICommand command = new PrefixAddCommand((Prefix)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<Prefix> items){
        ICommand command = new PrefixDeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Prefix item){
        ICommand command = new PrefixChangeCommand(item, tableView);
        command.execute();
    }

}
