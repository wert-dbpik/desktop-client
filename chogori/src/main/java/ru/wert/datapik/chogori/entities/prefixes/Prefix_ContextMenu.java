package ru.wert.datapik.chogori.entities.prefixes;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.chogori.entities.prefixes.commands._PrefixCommands;
import ru.wert.datapik.client.entity.models.Prefix;

import java.util.List;

public class Prefix_ContextMenu extends FormView_ContextMenu<Prefix> {

    private final _PrefixCommands commands;
    private Prefix_TableView tableView;

    public Prefix_ContextMenu(Prefix_TableView tableView, _PrefixCommands commands, String usersACCRes) {
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

        List<Prefix> selectedPrefixs = tableView.getSelectionModel().getSelectedItems();

        if (selectedPrefixs.size() == 0) {
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if (selectedPrefixs.size() > 1) {
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
