package ru.wert.datapik.chogori.entities.users;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.chogori.entities.users.commands._UserCommands;

import java.util.ArrayList;
import java.util.List;

public class User_ContextMenu extends FormView_ContextMenu<User> {

    private final _UserCommands commands;
    private User_TableView tableView;

    //Дополнительные пункты меню
    private MenuItem userActivity;

    public User_ContextMenu(User_TableView tableView, _UserCommands commands, String usersACCRes) {
        super(tableView, commands, usersACCRes);
        this.commands = commands;
        this.tableView = tableView;

        createMainMenuItems();
    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = true;
        boolean copyItem =true;
        boolean changeItem = true;
        boolean deleteItem = true;

        List<User> selectedUsers = tableView.getSelectionModel().getSelectedItems();

        if(selectedUsers.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedUsers.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems() {

        List<User> selectedUsers = tableView.getSelectionModel().getSelectedItems();
        List<MenuItem> extraItems = new ArrayList<>();

        userActivity = new MenuItem();
        boolean extraUserActivity = false;

        if(selectedUsers.size() == 1){
            extraUserActivity = true;
            if(selectedUsers.get(0).isActive()){
                userActivity.setText("Блокировать пользователя");
                userActivity.setOnAction(commands::blockUser);
            } else {
                userActivity.setText("Активировать пользователя");
                userActivity.setOnAction(commands::activateUser);
            }
        }

        if(extraUserActivity) extraItems.add(userActivity);

        return extraItems;
    }


}
