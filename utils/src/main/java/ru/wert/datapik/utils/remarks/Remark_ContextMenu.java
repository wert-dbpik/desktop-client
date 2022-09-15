package ru.wert.datapik.utils.remarks;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.remarks.commands._RemarkCommands;


import java.util.ArrayList;
import java.util.List;

public class Remark_ContextMenu extends FormView_ContextMenu<Remark> {

    private final _RemarkCommands commands;
    private Remark_TableView tableView;
    
    public Remark_ContextMenu(Remark_TableView tableView, _RemarkCommands commands, String usersACCRes) {
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

        List<Remark> selectedRemarks = tableView.getSelectionModel().getSelectedItems();

        if(selectedRemarks.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedRemarks.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems() {

        List<MenuItem> extraItems = new ArrayList<>();
        //НИЧЕГО НЕТ
        return extraItems;
    }


}
