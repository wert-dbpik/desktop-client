package ru.wert.datapik.chogori.entities.user_groups.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.entities.user_groups.UserGroup_TableView;

import java.util.List;

public class _UserGroupCommands implements ItemCommands<UserGroup> {

    private final UserGroup_TableView tableView;
    private String accWindowRes;

    public _UserGroupCommands(UserGroup_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, UserGroup newItem){
        ICommand command = new UserGroupAddCommand((UserGroup)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<UserGroup> items){
        ICommand command = new UserGroupDeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, UserGroup item){
        ICommand command = new UserGroupChangeCommand(item, tableView);
        command.execute();
    }

}
