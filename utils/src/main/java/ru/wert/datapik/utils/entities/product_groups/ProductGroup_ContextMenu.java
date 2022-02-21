package ru.wert.datapik.utils.entities.product_groups;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;

import java.util.ArrayList;
import java.util.List;

public class ProductGroup_ContextMenu extends FormView_ContextMenu<ProductGroup> {

    private final _ProductGroup_Commands commands;
    private Item_TreeView<Item, ProductGroup> treeView;
    private ProductGroup_Manipulator manipulator;

    private MenuItem cutItems;
    private MenuItem pasteItems;

    private MenuItem addPack;

    public ProductGroup_ContextMenu(ProductGroup_TreeView<Item> treeView, _ProductGroup_Commands commands, String productsACCRes) {
        super(treeView, commands, productsACCRes);
        this.commands = commands;
        this.treeView = treeView;

        this.manipulator = treeView.getManipulator();

        createMainMenuItems();

    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = true;
        boolean copyItem =true;
        boolean changeItem = true;
        boolean deleteItem = true;

        List<TreeItem<ProductGroup>> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

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

    @Override
    public List<MenuItem> createExtraItems(){
        boolean extraCutItems = false;
        boolean extraPasteItems = false;
        boolean extraAddPack = true;

        List<MenuItem> extraItems = new ArrayList<>();
        cutItems = new MenuItem("Вырезать");
        pasteItems = new MenuItem("Вставить");
        addPack = new MenuItem("Добавить пакет");

        cutItems.setOnAction(manipulator::cutItems);
        pasteItems.setOnAction(manipulator::pasteItems);
        addPack.setOnAction(commands::addProductToFolder);

        List<TreeItem<ProductGroup>> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(manipulator.pastePossible(null)) extraPasteItems = true;

        if(selectedTreeGroups.size() == 1){
            extraCutItems = true;
        }

        if (extraCutItems) extraItems.add(cutItems);
        if (extraPasteItems) extraItems.add(pasteItems);
        if ((extraCutItems || extraPasteItems) && extraAddPack) extraItems.add(new SeparatorMenuItem());
        if (extraAddPack) extraItems.add(addPack);

        return extraItems;
    }



}
