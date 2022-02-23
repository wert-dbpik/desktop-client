package ru.wert.datapik.utils.entities.folders;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.Catalogs;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class Folder_Manipulator {

    private Folder_TableView tableView;
    private ProductGroup_TreeView<Item> treeView;

    public Folder_Manipulator(Folder_TableView tableView, ProductGroup_TreeView<Item> treeView) {
        this.tableView = tableView;
        this.treeView = treeView;

        setOnKeyManipulator(tableView);

        createDragAndDropHandler();
    }

    public void createDragAndDropHandler(){

        tableView.setOnDragDetected(e -> {
            Dragboard db = tableView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(cutItems());
            db.setContent(content);

            String shownString = "папки";
            Text t = new Text(shownString);
            WritableImage image = t.snapshot(null, null);

            db.setDragViewOffsetY(25.0f);
            db.setDragView(image);

            e.consume();
        });

    }

    /**
     * Обработка события OnDragOver
     */
    public void createOnDragOver(TableRow<Item> row){
        row.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            tableView.getSelectionModel().clearAndSelect(row.getIndex());

            if (pastePossible(db.getString())) {
                event.acceptTransferModes(TransferMode.MOVE);
            } else{
                event.acceptTransferModes(TransferMode.NONE);
            }
            event.consume();
        });

    }

    /**
     * Обработка события OnDragDropped
     */
    public void createOnDragDropped(DragEvent event, TableRow<Item> row){
        Dragboard db = event.getDragboard();
        if(db.hasString()) {
            String str = db.getString();
            if (pastePossible(str)) {
                if (event.getTransferMode().equals(TransferMode.MOVE)) {
                    pasteItems(str);
                }
            }
        }
        event.consume();
    }

    /**
     * Управление с помощью клавиатуры
     * @param tableView Item_TreeView<Item, ProductGroup>
     */
    private void setOnKeyManipulator(Folder_TableView tableView) {
        tableView.setOnKeyPressed(e->{

            if (e.getCode() == KeyCode.DELETE) {
                List<Item> selectedItems = tableView.getSelectionModel().getSelectedItems();
                List<ProductGroup> selectedPG = new ArrayList<>();
                List<Folder> selectedF = new ArrayList<>();
                for (Item item : selectedItems) {
                    if (item instanceof ProductGroup)
                        selectedPG.add((ProductGroup) item);
                    else
                        selectedF.add((Folder) item);
                }

                if(!selectedF.isEmpty()) tableView.getCommands().delete(e, selectedF);
                if(!selectedPG.isEmpty()) treeView.getItemCommands().delete(e, selectedPG);
            }

            if ((e.getCode() == KeyCode.C && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isControlDown())) {
                String str = cutItems(); //(CTRL + C) вырезаем
                ClipboardUtils.copyToClipboardText(str);
            }

            if ((e.getCode() == KeyCode.V && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isShiftDown())) {
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
        //Флаг проверки первого PG в буфере обмена
        boolean pgPK = false;
        if(str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        String[] pasteData = str.split(" ", -1);
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
    public void pasteItems(String str){

        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);

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



}
