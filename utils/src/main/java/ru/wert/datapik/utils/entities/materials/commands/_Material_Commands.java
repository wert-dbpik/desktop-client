package ru.wert.datapik.utils.entities.materials.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.entities.materials.Material_TableView;

import java.util.List;

public class _Material_Commands implements ItemCommands<Material> {

    private final Material_TableView tableView;

    public _Material_Commands(Material_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Material newItem){
        ICommand command = new Material_AddCommand(newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<Material> items){
        ICommand command = new Material_DeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Material item){
        ICommand command = new Material_ChangeCommand(item, tableView);
        command.execute();
    }

}
