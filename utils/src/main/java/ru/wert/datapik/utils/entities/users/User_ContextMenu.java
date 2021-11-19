package ru.wert.datapik.utils.entities.users;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.users.commands._UserCommands;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class User_ContextMenu extends FormView_ContextMenu<User> {

    private final _UserCommands commands;
    private User_TableView tableView;

    private MenuItem killUser;
    private MenuItem throwOutAnEye;

    public User_ContextMenu(User_TableView tableView, _UserCommands commands, String usersACCRes) {
        super(tableView, commands, usersACCRes);
        this.commands = commands;
        this.tableView = tableView;

        createOnShowing();
    }

    @Override
    public void createOnShowing() {
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

        List<MenuItem> extraItems = new ArrayList<>();

        killUser = new MenuItem("Убить пользователя");
        killUser.setOnAction(commands::kill);

        throwOutAnEye = new MenuItem("Вырвать глаз");

        extraItems.add(killUser);
        extraItems.add(throwOutAnEye);

        return extraItems;
    }


}
