package ru.wert.datapik.utils.entities.users.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.entities.users.User_TableView;

import java.util.List;

public class _UserCommands implements ItemCommands<User> {

    private final User_TableView tableView;
    private String accWindowRes;

    public _UserCommands(User_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, User newItem){
        ICommand command = new UserAddCommand((User)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<User> items){
        ICommand command = new UserDeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, User item){
        ICommand command = new UserChangeCommand(item, tableView);
        command.execute();
    }

    public void kill(Event event){System.out.println("killed");}
}
