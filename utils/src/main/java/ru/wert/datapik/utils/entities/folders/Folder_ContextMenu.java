package ru.wert.datapik.utils.entities.folders;

import com.sun.deploy.util.FXLoader;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_ACCController;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_FOLDERS;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private final _Folder_Commands commands;
    private _ProductGroup_Commands productGroup_commands;
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

        setAddMenuName("Добавить пакет");
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

        productGroup_commands = new _ProductGroup_Commands(treeView, tableView, CH_FOLDERS);
        addProductGroup.setOnAction(this:: addNewProductGroup);
        changeProductGroup.setOnAction(this::changeProductGroup);
        deleteProductGroup.setOnAction(this::deleteProductGroups);


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

    private void addNewProductGroup(Event e){
        new FormViewACCWindow<ProductGroup>().create(EOperation.ADD, treeView, productGroup_commands, treeView.getAccWindowRes(), tableView);

     }

    private void changeProductGroup(Event e){
        Item chosenItem = tableView.getSelectionModel().getSelectedItem();
        if(chosenItem instanceof Folder) return;

        FormViewACCWindow<ProductGroup> formViewACCWindow = new FormViewACCWindow<>();
        formViewACCWindow.create(EOperation.CHANGE, treeView, productGroup_commands, treeView.getAccWindowRes(), tableView);


    }

    private void deleteProductGroups(Event e){
        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();
        List<ProductGroup> selectedItemGroups = new ArrayList<>();
        //преобразуем
        for(Item item : selectedItems){
            selectedItemGroups.add((ProductGroup)item);
        }
        productGroup_commands.delete(e, selectedItemGroups);
    }


}
