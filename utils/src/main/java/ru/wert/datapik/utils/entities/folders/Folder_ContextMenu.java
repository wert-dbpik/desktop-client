package ru.wert.datapik.utils.entities.folders;

import javafx.event.Event;
import javafx.scene.control.*;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.Catalogs;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.UPWARD;

public class Folder_ContextMenu extends FormView_ContextMenu<Folder> {

    private _ProductGroup_Commands productGroup_commands;
    private Folder_TableView tableView;
    private ProductGroup_TreeView<Item> treeView;

    private MenuItem cutItems;
    private MenuItem pasteItems;

    private MenuItem addProductGroup;
    private MenuItem changeProductGroup;
    private MenuItem deleteProductGroup;

    private String[] pasteData;


    public Folder_ContextMenu(Folder_TableView tableView, ProductGroup_TreeView treeView, _Folder_Commands commands, String productsACCRes) {
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
        boolean extraCutItems = false;
        boolean extraPasteItems = false;
        boolean extraAddProductGroup = false;
        boolean extraChangeProductGroup = false;
        boolean extraDeleteProductGroup = false;

        List<MenuItem> extraItems = new ArrayList<>();

        cutItems = new MenuItem("Вырезать");
        pasteItems = new MenuItem("Вставить");

        addProductGroup = new MenuItem("Добавить директорию");
        changeProductGroup = new MenuItem("Изменить");
        deleteProductGroup = new MenuItem("Удалить");

        productGroup_commands = new _ProductGroup_Commands(treeView, tableView, CH_FOLDERS);

        cutItems.setOnAction(this::cutItems);
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
                    extraCutItems = true;
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
                        extraCutItems = true;
                    }
                }
            } else { //selectedItems.get(0) instanceof Folder
                extraCutItems = true;
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
        if ((extraCutItems || extraPasteItems) && extraAddProductGroup) extraItems.add(new SeparatorMenuItem());
        if (extraAddProductGroup) extraItems.add(addProductGroup);
        if (extraChangeProductGroup) extraItems.add(changeProductGroup);
        if (extraDeleteProductGroup) extraItems.add(deleteProductGroup);


        return extraItems;
    }

    private void cutItems(Event e){
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

    /**
     *Нна лету проверяется возможность вставки
     * Вставка не возможна если:
     * 1) Если буфер обмена пустой
     * 2) Если буфер обмена не содержит в начале строки "pik!"
     * 3) Если сожержит классы отличные от ProductGroup(PG) и Folder(F)
     * 4) Если вставка производится в ту же группу или в группу потомка
     * @return true - вставка возможна
     */
    private boolean pastePossible(){
        //Строка в буфере обмена
        String str = ClipboardUtils.getString();
        //Флаг проверки первого PG в буфере обмена
        boolean pgPK = false;
        if(str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        pasteData = str.split(" ", -1);
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if(!clazz.equals("PG") && !clazz.equals("F"))
                return false;
            if(clazz.equals("PG") && !pgPK){
                //Вставляемый PG, найденный по его id
                Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
                ProductGroup pastedPG = CH_PRODUCT_GROUPS.findById(pastedItemId);
                //Элелемент каталога, куда производится вставка
                ProductGroup selectedPG;
                Item selectedItem = tableView.getSelectionModel().getSelectedItem();
                if(selectedItem == null)
                    //Если щелкнули по пустому месту
                    selectedPG = tableView.getSelectedTreeItem().getValue();
                else{
                    if(selectedItem instanceof ProductGroup)
                        selectedPG = (ProductGroup)selectedItem;
                    else
                        return false; //нельзя вставить в выделенный пакет (изделие)
                }
                TreeItem<ProductGroup> pastedTreeItem = treeView.findTreeItemById(pastedPG.getId());
                //Группа потомков с родителем, куда вставка не допускается
                List<ProductGroup> pastedItemChildren  = treeView.findAllGroupChildren(pastedTreeItem);
                pastedItemChildren.add(pastedPG);
                for(ProductGroup childPastedPG : pastedItemChildren){
                    if(childPastedPG.getId().equals(selectedPG.getId()))
                        return false;
                }
                //После пройденной проверки первого же PG меняем флаг, следущие PG проверяться не будут
                pgPK = true;
            }

        }


        return true;
    }

    /**
     * Собственно вставка элементов
     */
    private void pasteItems(Event e){
        List<Item> selectedItems = new ArrayList<>();
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
            ProductGroup selectedItem = ((ProductGroup)tableView.getSelectionModel().getSelectedItem());
            if(selectedItem == null) selectedItem = tableView.getSelectedTreeItem().getValue();

            if(clazz.equals("PG")){
                ProductGroup pg = CH_PRODUCT_GROUPS.findById(pastedItemId);
                pg.setParentId(selectedItem.getId());
                CH_PRODUCT_GROUPS.update(pg);
                selectedItems.add(pg);
            } else {
                Folder folder = CH_QUICK_FOLDERS.findById(pastedItemId);
                folder.setProductGroup(CH_PRODUCT_GROUPS.findById(selectedItem.getId()));
                CH_QUICK_FOLDERS.update(folder);
                selectedItems.add(folder);
            }
        }

        Catalogs.updateFormsWhenAddedOrChanged(treeView, tableView, selectedItems);

        ClipboardUtils.clear();
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
