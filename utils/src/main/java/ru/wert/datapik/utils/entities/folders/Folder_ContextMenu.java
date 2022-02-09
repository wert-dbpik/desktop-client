package ru.wert.datapik.utils.entities.folders;

import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TablePosition;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.utils.statics.AppStatic.UPWARD;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private _ProductGroup_Commands productGroup_commands;
    private Folder_TableView tableView;
    private ProductGroup_TreeView<Folder> treeView;

    private MenuItem eraseItems;
    private MenuItem pasteItems;

    private MenuItem addProductGroup;
    private MenuItem changeProductGroup;
    private MenuItem deleteProductGroup;


    public Folder_ContextMenu(Folder_TableView tableView, ProductGroup_TreeView<Folder> treeView, _Folder_Commands commands, String productsACCRes) {
        super((ItemTableView)tableView, commands, productsACCRes);
        this.tableView = tableView;
        this.treeView = treeView;

        createMainMenuItems();

    }


    @Override
    public void createMainMenuItems() {
        boolean addItem = false;
        boolean copyItem = false;
        boolean changeItem = false;
        boolean deleteItem = false;

        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            addItem = true;
        } else if (selectedItems.size() == 1) {
            if (selectedItems.get(0) instanceof Folder) {
                addItem = true;
                copyItem = true;
                changeItem = true;
                deleteItem = true;
            } else
                addItem = true;
        }else {
            int countFolders = 0;
            int countProductGroups = 0;
            for (Item item : selectedItems) {
                if (item instanceof ProductGroup)
                    countProductGroups++;
                else
                    countFolders++;
            }
            //Если выделены только значки с папкой в любом количестве > 0
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
        boolean extraEraseItems = true;
        boolean extraPasteItems = true;
        boolean extraAddProductGroup = false;
        boolean extraChangeProductGroup = false;
        boolean extraDeleteProductGroup = false;

        List<MenuItem> extraItems = new ArrayList<>();

        eraseItems = new MenuItem("Вырезать");
        pasteItems = new MenuItem("Вставить");

        addProductGroup = new MenuItem("Добавить директорию");
        changeProductGroup = new MenuItem("Изменить");
        deleteProductGroup = new MenuItem("Удалить");

        productGroup_commands = new _ProductGroup_Commands<>(treeView, tableView, CH_FOLDERS);

        eraseItems.setOnAction(this::eraseItems);
        pasteItems.setOnAction(this::pasteItems);
        addProductGroup.setOnAction(this:: addNewProductGroup);
        changeProductGroup.setOnAction(this::changeProductGroup);
        deleteProductGroup.setOnAction(this::deleteProductGroups);

        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();

        //Если ничего не выделено
        if (selectedItems.size() == 0) {
            extraAddProductGroup = true;
        //Если выделен значок с папкой и это не переход на уровень вверх
        } else if (selectedItems.size() == 1
               && selectedItems.get(0) instanceof ProductGroup){
            //Если выделенный элемент не самый верхний в таблице
            if (!selectedItems.get(0).equals(tableView.getItems().get(0))){
                extraAddProductGroup = true;
                extraChangeProductGroup = true;
                extraDeleteProductGroup = true;
            } else { //Если выделенный элемент  - самый верхний

                TablePosition<Item, Label> ts = tableView.getSelectionModel().getSelectedCells().get(0);

                String s = ((ts.getTableColumn().getCellData(0)).getText());
                //Если строка элемента не является < . . . >
                if(s.equals(UPWARD))
                    extraAddProductGroup = true;
                else{
                    extraAddProductGroup = true;
                    extraChangeProductGroup = true;
                    extraDeleteProductGroup = true;
                }
            }
            //Если выделено много элементов
        } else if (selectedItems.size() > 1) {
            int countFolders = 0;
            int countProductGroups = 0;
            for (Item item : selectedItems) {
                if (item instanceof ProductGroup)
                    countProductGroups++;
                else
                    countFolders++;
            }
            //Если выделено несколько значков с папкой
            if (countFolders == 0) {
                extraAddProductGroup = true;
                extraDeleteProductGroup = true;
            //Если выделена смесь значков с папкой и простых элементов
            } else if (countProductGroups == 0 || (countProductGroups > 0 && countFolders > 0)) {
                extraAddProductGroup = true;
            }
        }

        if(extraEraseItems) extraItems.add(eraseItems);
        if(extraPasteItems) extraItems.add(pasteItems);
        if(extraEraseItems || extraPasteItems) extraItems.add(new SeparatorMenuItem());
        if (extraAddProductGroup) extraItems.add(addProductGroup);
        if (extraChangeProductGroup) extraItems.add(changeProductGroup);
        if (extraDeleteProductGroup) extraItems.add(deleteProductGroup);



        return extraItems;
    }

    private void eraseItems(Event e){

    }

    private void pasteItems(Event e){

    }

    private void addNewProductGroup(Event e){
        new FormViewACCWindow<ProductGroup>()
                .create(EOperation.ADD, treeView, productGroup_commands, treeView.getAccWindowRes(), tableView, true);
     }

    private void changeProductGroup(Event e){
        Item chosenItem = tableView.getSelectionModel().getSelectedItem();
        if(chosenItem instanceof Folder) return;

        FormViewACCWindow<ProductGroup> formViewACCWindow = new FormViewACCWindow<>();
        formViewACCWindow
                .create(EOperation.CHANGE, treeView, productGroup_commands, treeView.getAccWindowRes(), tableView, true);
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
