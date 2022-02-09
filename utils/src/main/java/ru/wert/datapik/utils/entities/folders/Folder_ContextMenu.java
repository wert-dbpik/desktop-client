package ru.wert.datapik.utils.entities.folders;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.*;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.folders.commands.Folder_AddCommand;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands.ProductGroup_AddCommand;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
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

    private String[] pasteData;


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
        boolean extraEraseItems = false;
        boolean extraPasteItems = false;
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
            if (pastePossible()) extraPasteItems = true;
        } else if (selectedItems.size() == 1) {
            if (selectedItems.get(0) instanceof ProductGroup) {
                //Если выделенный элемент не самый верхний в таблице
                if (!selectedItems.get(0).equals(tableView.getItems().get(0))) {
                    extraAddProductGroup = true;
                    extraChangeProductGroup = true;
                    extraDeleteProductGroup = true;
                    extraEraseItems = true;
                    if (pastePossible()) extraPasteItems = true;
                } else {

                    TablePosition<Item, Label> ts = tableView.getSelectionModel().getSelectedCells().get(0);

                    String s = ((ts.getTableColumn().getCellData(0)).getText());
                    //Если строка элемента не является < . . . >
                    if (s.equals(UPWARD)) {
                        extraAddProductGroup = true;
                        if (pastePossible()) extraPasteItems = true;
                    } else {
                        extraAddProductGroup = true;
                        extraChangeProductGroup = true;
                        extraDeleteProductGroup = true;
                        extraEraseItems = true;
                    }
                }
            } else { //selectedItems.get(0) instanceof Folder
                extraEraseItems = true;
            }
            //Если выделено много элементов
        } else  { //selectedItems.size() > 1
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
                extraEraseItems = true;
            //Если выделена смесь значков с папкой и простых элементов
            } else if (countProductGroups == 0 || (countProductGroups > 0 && countFolders > 0)) {
                extraAddProductGroup = true;
                extraEraseItems = true;
            }
        }

        if (extraEraseItems) extraItems.add(eraseItems);
        if (extraPasteItems) extraItems.add(pasteItems);
        if ((extraEraseItems || extraPasteItems) && extraAddProductGroup) extraItems.add(new SeparatorMenuItem());
        if (extraAddProductGroup) extraItems.add(addProductGroup);
        if (extraChangeProductGroup) extraItems.add(changeProductGroup);
        if (extraDeleteProductGroup) extraItems.add(deleteProductGroup);


        return extraItems;
    }

    private void eraseItems(Event e){
        StringBuilder sb = new StringBuilder();
        sb.append("pik! ");
        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();
        for (Item item : selectedItems){
            if(item instanceof ProductGroup){
                sb.append("PG");
                sb.append("#");
                sb.append(item.getId());
                sb.append(" ");
            }
            else if(item instanceof Folder){
                sb.append("F");
                sb.append("#");
                sb.append(item.getId());
                sb.append(" ");
            }
        }
        ClipboardUtils.copyToClipboardText(sb.toString());
    }

    private boolean pastePossible(){
        String str = ClipboardUtils.getString();
        if(str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        pasteData = str.split(" ", -1);
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if(!clazz.equals("PG") && !clazz.equals("F"))
                return false;
        }
        return true;
    }

    private void pasteItems(Event e){
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            Long id = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
            ProductGroup selectedItem = ((ProductGroup)tableView.getSelectionModel().getSelectedItem());
            if(selectedItem == null) selectedItem = tableView.getSelectedTreeItem().getValue();

            if(clazz.equals("PG")){
                ProductGroup pg = CH_PRODUCT_GROUPS.findById(id);
                pg.setParentId(selectedItem.getId());
                CH_PRODUCT_GROUPS.update(pg);
            } else {
                Folder folder = CH_FOLDERS.findById(id);
                folder.setProductGroup(CH_PRODUCT_GROUPS.findById(selectedItem.getId()));
                CH_FOLDERS.update(folder);
            }
        }

        TreeItem<? extends CatalogGroup> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();
        Platform.runLater(() -> {
            treeView.updateView();
            treeView.getFocusModel().focus(selectedItemIndex - 1);
            treeView.scrollTo(selectedItemIndex - 1);
            if (tableView != null) {
                TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
                if(selectedTreeItemInTree != null && selectedTreeItemInTable.getValue().equals(selectedTreeItemInTree.getValue())) {
                    selectedTreeItemInTable = selectedTreeItemInTable.getParent();
                }
                tableView.updateOnlyTableView(selectedTreeItemInTable.getValue());
//                if (rowToBeSelectedAfterDeleting != null) {
//                    tableView.getSelectionModel().select(rowToBeSelectedAfterDeleting);
//                    tableView.scrollTo(rowToBeSelectedAfterDeleting);
//                }
            }
        });
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
