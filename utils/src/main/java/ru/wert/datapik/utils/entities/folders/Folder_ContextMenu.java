package ru.wert.datapik.utils.entities.folders;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;

import java.util.ArrayList;
import java.util.List;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private final _Folder_Commands commands;
    private TableView<Folder> tableView;

    private MenuItem lickBoots;
    private MenuItem blueWaser;


    public Folder_ContextMenu(Folder_TableView tableView, _Folder_Commands commands, String productsACCRes) {
        super(tableView, commands, productsACCRes);
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

        List<Folder> selectedFolders = tableView.getSelectionModel().getSelectedItems();

        if(selectedFolders.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedFolders.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems(){

        List<MenuItem> extraItems = new ArrayList<>();
        lickBoots = new MenuItem("Лизать сапог");

        lickBoots.setOnAction(commands::lick);
        blueWaser = new MenuItem("Блевать в вазу");

        List<Folder> selectedFolders = tableView.getSelectionModel().getSelectedItems();

        if(selectedFolders.size() == 1) extraItems.add(lickBoots);
        if(selectedFolders.size() == 1) extraItems.add(blueWaser);

        return extraItems;
    }


}
