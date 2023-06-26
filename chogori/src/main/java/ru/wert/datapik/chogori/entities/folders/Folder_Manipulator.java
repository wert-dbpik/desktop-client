package ru.wert.datapik.chogori.entities.folders;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.commands.Catalogs;
import ru.wert.datapik.chogori.common.utils.ClipboardUtils;
import ru.wert.datapik.chogori.entities.drafts.Draft_TableView;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;

public class Folder_Manipulator {

    private Folder_TableView tableView;
    private ProductGroup_TreeView<Item> treeView;
    @Setter private Draft_TableView draftTable;


    public Folder_Manipulator(Folder_TableView tableView, ProductGroup_TreeView<Item> treeView) {
        this.tableView = tableView;
        this.treeView = treeView;

        setOnKeyManipulator(tableView);
    }

    public void createOnDragDetected(Event event) {
        Dragboard db = tableView.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.putString(cutItems());
        db.setContent(content);

        String shownString = "Комплекты чертежей";
        Text t = new Text(shownString);
        WritableImage image = t.snapshot(null, null);

        db.setDragViewOffsetY(25.0f);
        db.setDragView(image);

        event.consume();
    }

    /**
     * Обработка события OnDragOver
     */
    public void createOnDragOver(DragEvent event, TableRow<Item> row) {

        Dragboard db = event.getDragboard();
        tableView.getSelectionModel().clearAndSelect(row.getIndex());

        if (pastePossible(db.getString())) {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        } else {
            event.acceptTransferModes(TransferMode.NONE);
        }

    }

    /**
     * Обработка события OnDragDropped
     */
    public void createOnDragDropped(DragEvent event){
        Dragboard db = event.getDragboard();
        if(db.hasString()) {
            String str = db.getString();
            if (pastePossible(str)) {
                if (event.getTransferMode().equals(TransferMode.MOVE)) {
                    pasteItems(str);
                    event.consume();
                }
            }
        }

    }

    /**
     * Управление с помощью клавиатуры
     * @param tableView Item_TreeView<Item, ProductGroup>
     */
    private void setOnKeyManipulator(Folder_TableView tableView) {
        tableView.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Item prevRowData = null;
                Item clickedItem = tableView.getSelectionModel().getSelectedItem();

                if(clickedItem instanceof ProductGroup){
                    if(clickedItem == tableView.getUpwardRow().getValue()){ //Верхняя строка
                        prevRowData = clickedItem;
                        tableView.setUpwardRow(treeView.findTreeItemById(((ProductGroup) clickedItem).getParentId()));
                    } else {
                        tableView.setUpwardRow(treeView.findTreeItemById(clickedItem.getId()));
                    }
                    tableView.updateNow((ProductGroup) prevRowData);
                }

            }

            if (e.getCode() == KeyCode.DELETE && tableView.isUseContextMenu()) {
                List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();
                List<ProductGroup> selectedPG = new ArrayList<>();
                List<Item> selectedF = new ArrayList<>();
                for (Item item : selectedItems) {
                    if (item instanceof ProductGroup)
                        selectedPG.add((ProductGroup) item);
                    else
                        selectedF.add(item);
                }

                if(!selectedF.isEmpty()) tableView.getCommands().delete(e, selectedF);
                if(!selectedPG.isEmpty()) treeView.getItemCommands().delete(e, selectedPG);
            }

            if ((e.getCode() == KeyCode.C && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isControlDown())
                    && tableView.isUseContextMenu()) {
                String str = cutItems(); //(CTRL + C) вырезаем
                ClipboardUtils.copyToClipboardText(str);
            }

            if ((e.getCode() == KeyCode.V && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isShiftDown())
                    && tableView.isUseContextMenu()) {
                String str = ClipboardUtils.getStringFromClipboard();
                if(pastePossible(str)) pasteItems(str); //(CTRL + V) вставляем
            }
        });
    }

    public String cutItems(){
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
        return sb.toString();
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
    public boolean pastePossible(String str){
        //Флаг проверки первого PG в буфере обмена - проверяем только первый
        boolean pgPK = false;
        if(str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        String[] pasteData = str.split(" ", -1);
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            Item selectedItem = tableView.getSelectionModel().getSelectedItem();
            if(!clazz.equals("PG") && !clazz.equals("F") && !clazz.equals("DR"))
                return false;
            if(clazz.equals("PG") && !pgPK){
                //Вставляемый PG, найденный по его id
                Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
                ProductGroup pastedPG = CH_PRODUCT_GROUPS.findById(pastedItemId);
                //Элелемент каталога, куда производится вставка
                ProductGroup selectedPG;

                if(selectedItem == null)
                    //Если щелкнули по пустому месту
                    selectedPG = tableView.getUpwardRow().getValue();
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
            else if(clazz.equals("DR")){
                //Чертеж можем добавлять только в комплект (папку)
                if(selectedItem == null || selectedItem instanceof ProductGroup)
                    return false;
            }

        }

        return true;
    }

    /**
     * Собственно вставка элементов
     */
    public void pasteItems(String str){

        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);

        List<Item> selectedItems = new ArrayList<>(); //
        for(String s : pasteData){
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));

            Item selectedItem = tableView.getSelectionModel().getSelectedItem();
            if(selectedItem == null) { //Ничего не выделено
                selectedItem = tableView.getUpwardRow().getValue();
                switch (clazz) {
                    case "DR":
                        return;
                    case "PG":
                        ProductGroup pg = CH_PRODUCT_GROUPS.findById(pastedItemId);
                        pg.setParentId(selectedItem.getId());
                        CH_PRODUCT_GROUPS.update(pg);
                        selectedItems.add(pg);
                        break;
                    case "F":
                        Folder folder = CH_QUICK_FOLDERS.findById(pastedItemId);
                        folder.setProductGroup(CH_PRODUCT_GROUPS.findById(selectedItem.getId()));
                        CH_QUICK_FOLDERS.update(folder);
                        selectedItems.add(folder);
                        break;
                }

                Catalogs.updateFormsWhenAddedOrChanged(treeView, tableView, selectedItems);

            } else {
                if (selectedItem instanceof ProductGroup) { //Если при вставке выделена папка с комплектами
                    ProductGroup selectedGroup = (ProductGroup) selectedItem;

                    switch (clazz) {
                        case "PG":
                            ProductGroup pg = CH_PRODUCT_GROUPS.findById(pastedItemId);
                            pg.setParentId(selectedItem.getId());
                            CH_PRODUCT_GROUPS.update(pg);
                            selectedItems.add(pg);
                            break;
                        case "F":
                            Folder folder = CH_QUICK_FOLDERS.findById(pastedItemId);
                            folder.setProductGroup(CH_PRODUCT_GROUPS.findById(selectedGroup.getId()));
                            CH_QUICK_FOLDERS.update(folder);
                            selectedItems.add(folder);
                            break;
                    }

                    Catalogs.updateFormsWhenAddedOrChanged(treeView, tableView, selectedItems);

                } else if (selectedItem instanceof Folder) { //Если выделен комплект
                    if (clazz.equals("DR")) {
                        Folder selectedFolder = (Folder) selectedItem;
                        Draft draft = CH_QUICK_DRAFTS.findById(pastedItemId);
                        draft.setFolder(selectedFolder);
                        CH_QUICK_DRAFTS.update(draft);
                        Platform.runLater(() ->
                                tableView.getDraftTable().updateRoutineTableView(Collections.singletonList(draft), false));
                    } else if (clazz.equals("F")) {
                        Folder folder = CH_QUICK_FOLDERS.findById(pastedItemId);
                        folder.setProductGroup(CH_PRODUCT_GROUPS.findById(tableView.getUpwardRow().getValue().getId()));
                        CH_QUICK_FOLDERS.update(folder);
                        selectedItems.add(folder);

                        Catalogs.updateFormsWhenAddedOrChanged(treeView, tableView, selectedItems);
                    }
                }
            }

        }



        ClipboardUtils.clear();
    }

}
