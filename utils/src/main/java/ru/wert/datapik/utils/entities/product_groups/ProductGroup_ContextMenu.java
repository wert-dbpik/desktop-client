package ru.wert.datapik.utils.entities.product_groups;

import javafx.scene.control.MenuItem;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;

import java.util.ArrayList;
import java.util.List;

public class ProductGroup_ContextMenu extends FormView_ContextMenu<ProductGroup> {

    private final _ProductGroup_Commands commands;
    private ProductGroup_TreeView treeView;

    private MenuItem mAddProduct;

    public ProductGroup_ContextMenu(ProductGroup_TreeView treeView, _ProductGroup_Commands commands, String productsACCRes) {
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

        List<ProductGroup> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(selectedTreeGroups.size() == 0){
            copyItem = false;
            changeItem = false;
            deleteItem = false;
        } else if(selectedTreeGroups.size() > 1){
            copyItem = false;
            changeItem = false;
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);

        setAddMenuName("Добавить директорию");
    }

    public List<MenuItem> createExtraItems(){

        List<MenuItem> extraItems = new ArrayList<>();
        mAddProduct = new MenuItem("Добавить пакет");
        mAddProduct.setOnAction(commands::addProductToFolder);

        List<ProductGroup> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(selectedTreeGroups.size() >= 1) extraItems.add(mAddProduct);

        return extraItems;
    }


}
