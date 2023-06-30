package ru.wert.tubus.chogori.entities.densities.commands;

import javafx.event.Event;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.entities.densities.Density_TableView;
import ru.wert.tubus.client.entity.models.Density;

import java.util.List;

public class _DensityCommands implements ItemCommands<Density> {

    private final Density_TableView tableView;
    private String accWindowRes;

    public _DensityCommands(Density_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Density newItem){
        ICommand command = new DensityAddCommand((Density)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<Density> items){
        ICommand command = new DensityDeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Density item){
        ICommand command = new DensityChangeCommand(item, tableView);
        command.execute();
    }

}
