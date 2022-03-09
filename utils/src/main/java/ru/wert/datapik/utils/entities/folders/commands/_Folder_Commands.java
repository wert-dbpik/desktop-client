package ru.wert.datapik.utils.entities.folders.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;

import java.util.List;

public class _Folder_Commands implements ItemCommands<Folder> {

    private final Folder_TableView tableView;

    public _Folder_Commands(Folder_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Folder newItem){
        ICommand command = new Folder_AddCommand(newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<Folder> items){
        ICommand command = new Folder_DeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Folder item){
        ICommand command = new Folder_ChangeCommand(item, tableView);
        command.execute();
    }

}
