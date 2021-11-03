package ru.wert.datapik.utils.entities.materials;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.materials.commands._Material_Commands;

import java.util.ArrayList;
import java.util.List;

public class Material_ContextMenu extends FormView_ContextMenu<Material> {

    private final _Material_Commands commands;
    private Material_TableView tableView;

    public Material_ContextMenu(Material_TableView tableView, _Material_Commands commands, String MaterialsACCRes) {
        super(tableView, commands, MaterialsACCRes);
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

        List<Material> selectedMaterials = tableView.getSelectionModel().getSelectedItems();

        if(selectedMaterials.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedMaterials.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems() {

        List<MenuItem> extraItems = new ArrayList<>();

//        killMaterial = new MenuItem("Убить пользователя");
//
//
//        throwOutAnEye = new MenuItem("Вырвать глаз");
//
//        extraItems.add(killMaterial);
//        extraItems.add(throwOutAnEye);

        return extraItems;
    }


}
