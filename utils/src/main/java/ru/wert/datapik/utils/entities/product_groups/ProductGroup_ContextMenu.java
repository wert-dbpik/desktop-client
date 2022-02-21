package ru.wert.datapik.utils.entities.product_groups;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.Catalogs;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class ProductGroup_ContextMenu extends FormView_ContextMenu<ProductGroup> {

    private final _ProductGroup_Commands commands;
    private Item_TreeView<Item, ProductGroup> treeView;

    private MenuItem cutItems;
    private MenuItem pasteItems;

    private MenuItem addPack;

    private String[] pasteData;

    public ProductGroup_ContextMenu(ProductGroup_TreeView<Item> treeView, _ProductGroup_Commands commands, String productsACCRes) {
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

        cutItems.setOnAction(this::cutItems);
        pasteItems.setOnAction(this::pasteItems);
        addPack.setOnAction(commands::addProductToFolder);

        List<TreeItem<ProductGroup>> selectedTreeGroups = treeView.getSelectionModel().getSelectedItems();

        if(pastePossible()) extraPasteItems = true;

        if(selectedTreeGroups.size() == 1){
            extraCutItems = true;
        }

        if (extraCutItems) extraItems.add(cutItems);
        if (extraPasteItems) extraItems.add(pasteItems);
        if ((extraCutItems || extraPasteItems) && extraAddPack) extraItems.add(new SeparatorMenuItem());
        if (extraAddPack) extraItems.add(addPack);

        return extraItems;
    }

    private void cutItems(Event e) {
        StringBuilder sb = new StringBuilder();
        sb.append("pik! ");
        ProductGroup selectedItem = treeView.getSelectionModel().getSelectedItem().getValue();
        sb.append("PG");
        sb.append("#");
        sb.append(selectedItem.getId());
        sb.append(" ");

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
        TreeItem<ProductGroup> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if(selectedItem == null) selectedItem = treeView.getRoot();
        List<ProductGroup> children = treeView.findAllGroupChildren(selectedItem);
        //Флаг проверки первого PG в буфере обмена
        boolean pgPK = false;
        String str = ClipboardUtils.getString();
        //1)ClipboardUtils = null или 2)Начинается НЕ с "pik!"
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
                ProductGroup selectedPG = ((ProductGroup)treeView.getSelectionModel().getSelectedItem().getValue());
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

    private void pasteItems(Event e) {
        List<Item> selectedItems = new ArrayList<>();
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
            TreeItem<ProductGroup> ti = treeView.getSelectionModel().getSelectedItem();
            //При выделении заготовка КАТАЛОГ
            if(ti == null) ti = treeView.getRoot();
            ProductGroup selectedItem = ti.getValue();
            if (clazz.equals("PG")) {
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

        Catalogs.updateFormsWhenAddedOrChanged(treeView, treeView.getConnectedForm(), selectedItems);

        ClipboardUtils.clear();
    }

}
