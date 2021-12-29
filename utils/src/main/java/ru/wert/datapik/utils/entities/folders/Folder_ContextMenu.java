package ru.wert.datapik.utils.entities.folders;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;

import java.util.ArrayList;
import java.util.List;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private final _Folder_Commands commands;
    private Folder_TableView tableView;
    private ProductGroup_TreeView<ProductGroup> treeView;

    private MenuItem addProductGroup;
    private MenuItem changeProductGroup;
    private MenuItem deleteProductGroup;


    public Folder_ContextMenu(Folder_TableView tableView, ProductGroup_TreeView<ProductGroup> treeView, _Folder_Commands commands, String productsACCRes) {
        super((ItemTableView)tableView, commands, productsACCRes);
        this.commands = commands;
        this.tableView = tableView;
        this.treeView = treeView;

        createOnShowing();

    }

    @Override
    public void createOnShowing() {
        boolean addItem = false;
        boolean copyItem = false;
        boolean changeItem = false;
        boolean deleteItem = false;

        List<Item> selectedFolders = tableView.getSelectionModel().getSelectedItems();

        if (selectedFolders.size() == 0) {
            addItem = true;
        } else if (selectedFolders.size() == 1) {
            if (selectedFolders.get(0) instanceof Folder) {
                addItem = true;
                copyItem = true;
                changeItem = true;
                deleteItem = true;
            } else
                addItem = true;
        }else {
            int countFolders = 0;
            int countProductGroups = 0;
            for (Item item : selectedFolders) {
                if (item instanceof ProductGroup)
                    countProductGroups++;
                else
                    countFolders++;
            }
            if(countFolders == 0 || (countFolders > 0 && countProductGroups > 0)){
                addItem = true;
            } else if(countProductGroups == 0){
                deleteItem = true;
                addItem = true;
            }
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);
    }

    @Override
    public List<MenuItem> createExtraItems(){
        boolean extraAddProductGroup = false;
        boolean extraChangeProductGroup = false;
        boolean extraDeleteProductGroup = false;


        List<MenuItem> extraItems = new ArrayList<>();
        addProductGroup = new MenuItem("Добавить директорию");
        changeProductGroup = new MenuItem("Изменить");
        deleteProductGroup = new MenuItem("Удалить");

        _ProductGroup_Commands productGroup_commands = new _ProductGroup_Commands(treeView);
//        addProductGroup.setOnAction(productGroup_commands::add);
//        changeProductGroup.setOnAction(productGroup_commands.change);
//        deleteProductGroup.setOnAction(productGroup_commands::delete);


        List<Item> selectedFolders = tableView.getSelectionModel().getSelectedItems();

        if (selectedFolders.size() == 0 || (selectedFolders.size() == 1 && !(selectedFolders.get(0) instanceof ProductGroup))) {
            extraAddProductGroup = true;
        } else if (selectedFolders.size() == 1 && selectedFolders.get(0) instanceof ProductGroup){
            extraAddProductGroup = true;
            extraChangeProductGroup = true;
            extraDeleteProductGroup = true;
        } else if (selectedFolders.size() > 1) {
            int countFolders = 0;
            int countProductGroups = 0;
            for (Item item : selectedFolders) {
                if (item instanceof ProductGroup)
                    countProductGroups++;
                else
                    countFolders++;
            }
            if (countFolders == 0) {
                extraAddProductGroup = true;
                extraDeleteProductGroup = true;
            } else if (countProductGroups == 0 || (countProductGroups > 0 && countFolders > 0)) {
                extraAddProductGroup = true;
            }
        }


        if (extraAddProductGroup) extraItems.add(addProductGroup);
        if (extraChangeProductGroup) extraItems.add(changeProductGroup);
        if (extraDeleteProductGroup) extraItems.add(deleteProductGroup);

        return extraItems;
    }


}
