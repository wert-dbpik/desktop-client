package ru.wert.datapik.chogori.entities.material_groups;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.chogori.entities.material_groups.commands._MaterialGroup_Commands;

import java.util.ArrayList;
import java.util.List;

public class MaterialGroup_ContextMenu extends FormView_ContextMenu<MaterialGroup> {

    private final _MaterialGroup_Commands commands;
    private MaterialGroup_TreeView treeView;

    private MenuItem mAddProduct;

    public MaterialGroup_ContextMenu(MaterialGroup_TreeView treeView, _MaterialGroup_Commands commands, String productsACCRes) {
        super(treeView, commands, productsACCRes);
        this.commands = commands;
        this.treeView = treeView;

        createMainMenuItems();

    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = true;
        boolean copyItem =true;
        boolean changeItem = true;
        boolean deleteItem = true;

        List<MaterialGroup> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(selectedTreeGroups.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedTreeGroups.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    public List<MenuItem> createExtraItems(){

        List<MenuItem> extraItems = new ArrayList<>();
        mAddProduct = new MenuItem("Добавить изделие");
        mAddProduct.setOnAction(commands::addProductToFolder);

        List<MaterialGroup> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(selectedTreeGroups.size() >= 1) extraItems.add(mAddProduct);

        return extraItems;
    }


}
