package ru.wert.datapik.chogori.entities.passports.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.entities.passports.Passport_TableView;


import java.util.List;

public class _Passport_Commands implements ItemCommands<Passport> {

    private final Passport_TableView tableView;
    private String accWindowRes;

    public _Passport_Commands(Passport_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Passport newItem){
        ICommand command = new Passport_AddCommand((Passport)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<Passport> items){
        ICommand command = new Passport_DeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Passport item){
        ICommand command = new Passport_ChangeCommand(item, tableView);
        command.execute();
    }

}