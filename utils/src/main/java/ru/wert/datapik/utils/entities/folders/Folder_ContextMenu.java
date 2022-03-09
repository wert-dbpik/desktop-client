package ru.wert.datapik.utils.entities.folders;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.UPWARD;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private _ProductGroup_Commands productGroup_commands;
    private Folder_TableView tableView;
    private ProductGroup_TreeView<Item> treeView;

    private MenuItem cutItems;
    private MenuItem pasteItems;

    private MenuItem addProductGroup;
    private MenuItem changeProductGroup;
    private MenuItem deleteProductGroup;

    private MenuItem showFolderInCatalog;

    Folder_Manipulator manipulator;



    public Folder_ContextMenu(Folder_TableView tableView, ProductGroup_TreeView treeView, _Folder_Commands commands, String productsACCRes) {
        super((ItemTableView)tableView, commands, productsACCRes);
        this.tableView = tableView;
        this.treeView = treeView;

        manipulator = tableView.getManipulator();

        createMainMenuItems();

    }


    @Override
    public void createMainMenuItems() {
        boolean addItem = false;
        boolean copyItem = false;
        boolean changeItem = false;
        boolean deleteItem = false;

        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();

        //Условие при котором таблица не отображает только папки (globalOn)
        // и в таблице не ведется поиск
        boolean noSearchAndGlobal = CH_SEARCH_FIELD.getText().isEmpty() && tableView.isGlobalOff();

        if (selectedItems.size() == 0) {
            if (noSearchAndGlobal) addItem = true;
        } else if (selectedItems.size() == 1) {
            if (selectedItems.get(0) instanceof Folder) {
                changeItem = true;
                deleteItem = true;
                if(noSearchAndGlobal) {
                    addItem = true;
                    copyItem = true;
                }
            } else {
                if (noSearchAndGlobal) addItem = true;
            }
        }else {  //selectedItems.size() > 1
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
                if (noSearchAndGlobal) addItem = true;

            } else if(countProductGroups == 0){
                deleteItem = true;
                if (noSearchAndGlobal) addItem = true;
            }
        }

        createMenu(addItem, copyItem, changeItem, deleteItem);

        setAddMenuName("Добавить пакет");
    }



    @Override
    public List<MenuItem> createExtraItems(){
        boolean extraCutItems = false;
        boolean extraPasteItems = false;
        boolean extraAddProductGroup = false;
        boolean extraChangeProductGroup = false;
        boolean extraDeleteProductGroup = false;
        boolean extraShowFolderInCatalog = false;

        List<MenuItem> extraItems = new ArrayList<>();

        cutItems = new MenuItem("Вырезать");
        pasteItems = new MenuItem("Вставить");

        addProductGroup = new MenuItem("Добавить директорию");
        changeProductGroup = new MenuItem("Изменить");
        deleteProductGroup = new MenuItem("Удалить");
        showFolderInCatalog = new MenuItem("Показать комплект в каталоге");

        productGroup_commands = new _ProductGroup_Commands(treeView, tableView, CH_FOLDERS);

        cutItems.setOnAction(e-> ClipboardUtils.copyToClipboardText(manipulator.cutItems()));
        pasteItems.setOnAction(e->manipulator.pasteItems(ClipboardUtils.getStringFromClipboard()));
        addProductGroup.setOnAction(this:: addNewProductGroup);
        changeProductGroup.setOnAction(this::changeProductGroup);
        deleteProductGroup.setOnAction(this::deleteProductGroups);
        showFolderInCatalog.setOnAction(this::showFolderInCatalog);

        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();

        //Условие при котором таблица не отображает только папки (globalOn)
        // и в таблице не ведется поиск
        boolean noSearchAndGlobal = CH_SEARCH_FIELD.getText().isEmpty() && !tableView.isGlobalOff();
        //Условие при котором вставка допускается
        boolean pastePossible = manipulator.pastePossible(ClipboardUtils.getStringFromClipboard());

        //Если ничего не выделено
        if (selectedItems.size() == 0) {
            extraAddProductGroup = true;
            if (pastePossible) extraPasteItems = true;
        } else if (selectedItems.size() == 1) {
            if(!noSearchAndGlobal && selectedItems instanceof Folder){
                extraShowFolderInCatalog = true;
            }
            if (selectedItems.get(0) instanceof ProductGroup) {
                //Если выделенный элемент не самый верхний в таблице
                if (!selectedItems.get(0).equals(tableView.getItems().get(0))) {
                    extraAddProductGroup = true;
                    extraChangeProductGroup = true;
                    extraDeleteProductGroup = true;
                    extraCutItems = true;
                    if (pastePossible) extraPasteItems = true;
                } else {

                    TablePosition<Item, Label> ts = tableView.getSelectionModel().getSelectedCells().get(0);
                    TableColumn<Item, Label> col = ts.getTableColumn();
                    String s = "";
                    if(col != null){ //В этом месте выскакивает nullPointException, связанное с верхней строкой таблицы
                        Label label = ts.getTableColumn().getCellData(0);
                        s = label.getText();
                    }
                    //Если строка элемента не является < . . . >
                    if (s.equals(UPWARD)) {
                        extraAddProductGroup = true;
                        if (pastePossible) extraPasteItems = true;
                    } else {
                        extraAddProductGroup = true;
                        extraChangeProductGroup = true;
                        extraDeleteProductGroup = true;
                        extraCutItems = true;
                    }
                }
            } else { //selectedItems.get(0) instanceof Folder
                extraCutItems = true;
                if (pastePossible) extraPasteItems = true;
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
                extraCutItems = true;
            //Если выделена смесь значков с папкой и простых элементов
            } else if (countProductGroups == 0 || (countProductGroups > 0 && countFolders > 0)) {
                extraAddProductGroup = true;
                extraCutItems = true;
            }
        }

        if (extraCutItems) extraItems.add(cutItems);
        if (extraPasteItems) extraItems.add(pasteItems);
        if ((extraAddProductGroup || extraChangeProductGroup || extraDeleteProductGroup)
                && (extraCutItems || extraPasteItems))
            extraItems.add(new SeparatorMenuItem());
        if (extraAddProductGroup) extraItems.add(addProductGroup);
        if (extraChangeProductGroup) extraItems.add(changeProductGroup);
        if (extraDeleteProductGroup) extraItems.add(deleteProductGroup);
        if (extraShowFolderInCatalog) extraItems.add(new SeparatorMenuItem());
        if (extraShowFolderInCatalog) extraItems.add(showFolderInCatalog);

        return extraItems;
    }

    /**
     * В методе определяется выделенная папка, определяется группа в которую входит эта папка
     * и обновление таблицы с учетом группы. В полученном списке определяется индекс искомой папки
     * И ее выделение
     */
    private void showFolderInCatalog(ActionEvent actionEvent) {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if(selectedItem instanceof Folder) {
            CH_SEARCH_FIELD.setText("");
            tableView.updateVisibleLeafOfTableView(((Folder)selectedItem).getProductGroup());
            int index = tableView.getItems().indexOf(selectedItem);

            tableView.getSelectionModel().select(index);
        }

    }

    public void deleteProductGroups(Event e){
        List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();
        List<ProductGroup> selectedItemGroups = new ArrayList<>();
        //преобразуем
        for(Item item : selectedItems){
            selectedItemGroups.add((ProductGroup)item);
        }
        treeView.getItemCommands().delete(e, selectedItemGroups);
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


}
