package ru.wert.tubus.chogori.entities.densities;

import javafx.scene.control.MenuItem;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.tubus.chogori.entities.densities.commands._DensityCommands;
import ru.wert.tubus.client.entity.models.Density;

import java.util.List;

public class Density_ContextMenu extends FormView_ContextMenu<Density> {

    private final _DensityCommands commands;
    private Density_TableView tableView;

    public Density_ContextMenu(Density_TableView tableView, _DensityCommands commands, String usersACCRes) {
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

        List<Density> selectedDensitys = tableView.getSelectionModel().getSelectedItems();

        if (selectedDensitys.size() == 0) {
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if (selectedDensitys.size() > 1) {
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
