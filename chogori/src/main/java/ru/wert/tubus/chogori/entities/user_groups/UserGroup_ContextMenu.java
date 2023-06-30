package ru.wert.tubus.chogori.entities.user_groups;

import javafx.scene.control.MenuItem;
import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.tubus.chogori.entities.user_groups.commands._UserGroupCommands;


import java.util.List;

public class UserGroup_ContextMenu extends FormView_ContextMenu<UserGroup> {

    private final _UserGroupCommands commands;
    private UserGroup_TableView tableView;

    public UserGroup_ContextMenu(UserGroup_TableView tableView, _UserGroupCommands commands, String usersACCRes) {
        super(tableView, commands, usersACCRes);
        this.commands = commands;
        this.tableView = tableView;

        createMainMenuItems();
    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = true;
        boolean copyItem = true;
        boolean changeItem = true;
        boolean deleteItem = true;

        List<UserGroup> selectedUserGroups = tableView.getSelectionModel().getSelectedItems();

        if (selectedUserGroups.size() == 0) {
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if (selectedUserGroups.size() > 1) {
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems() {

        //Дополнительных нет!
        return null;
    }


}
