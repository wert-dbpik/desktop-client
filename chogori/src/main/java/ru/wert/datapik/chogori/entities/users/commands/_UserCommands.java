package ru.wert.datapik.chogori.entities.users.commands;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.entities.users.User_TableView;

import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_USERS;

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
        CH_USERS.update(user);
        Platform.runLater(()->tableView.updateRoutineTableView(Collections.singletonList(user), false));
    }

    public void activateUser(ActionEvent event) {
        User user = tableView.getSelectionModel().getSelectedItem();
        user.setActive(true);
        CH_USERS.update(user);
        Platform.runLater(()->tableView.updateRoutineTableView(Collections.singletonList(user), false));
    }
}
