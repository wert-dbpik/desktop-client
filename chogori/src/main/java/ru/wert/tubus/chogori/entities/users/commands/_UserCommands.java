package ru.wert.tubus.chogori.entities.users.commands;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.entities.users.User_TableView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;

import java.util.Collections;
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

    public void blockUser(Event event){
        User user = tableView.getSelectionModel().getSelectedItem();
        user.setActive(false);
        ChogoriServices.CH_USERS.update(user);
        Platform.runLater(()->tableView.updateRoutineTableView(Collections.singletonList(user), false));
    }

    public void activateUser(ActionEvent event) {
        User user = tableView.getSelectionModel().getSelectedItem();
        user.setActive(true);
        ChogoriServices.CH_USERS.update(user);
        Platform.runLater(()->tableView.updateRoutineTableView(Collections.singletonList(user), false));
    }
}
