package ru.wert.datapik.utils.entities.product_groups;

import javafx.event.Event;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.Catalogs;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ProductGroup_Manipulator {

    private Item_TreeView<Item, ProductGroup> treeView;
    private ItemTableView<Item> tableView;

    private TreeItem<ProductGroup> hoveredItem = null;
    private Thread hoveredThread;

    public ProductGroup_Manipulator(Item_TreeView<Item, ProductGroup> treeView, ItemTableView<Item> tableView) {
        this.treeView = treeView;
        this.tableView = tableView;

        if(CH_CURRENT_USER.getUserGroup().isEditDrafts())
            setOnKeyManipulator(treeView);

        createDragAndDropHandler();
    }

    public void createDragAndDropHandler(){
        treeView.setCellFactory(new Callback<TreeView<ProductGroup>, TreeCell<ProductGroup>>() {
            @Override
            public TreeCell<ProductGroup> call(TreeView<ProductGroup> param) {
                TreeCell<ProductGroup> treeCell = new TreeCell<ProductGroup>() {
                    protected void updateItem(ProductGroup item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            //Остаются исходные данные
                            setText(item.getName());
                            setGraphic(new ImageView(TREE_NODE_IMG));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };

                treeCell.setOnDragDetected(e -> createOnDragDetected(e, treeCell));
                treeCell.setOnDragOver(e -> createOnDragOver(e, treeCell));
                treeCell.setOnDragDropped(e -> createOnDragDropped(e, treeCell));

                return treeCell;
            }
        });
    }

    private void createOnDragDetected(MouseEvent e, TreeCell<ProductGroup> treeCell){

        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
        TreeItem<ProductGroup> draggedItem = treeCell.getTreeItem();

        if(draggedItem != null){
            ProductGroup draggedPG = draggedItem.getValue();

            ClipboardContent content = ClipboardUtils.copyToClipboardText("pik! PG#" + draggedPG.getId());
            db.setContent(content);

            //Преобразуем название treeCell в изображение, чтобы добавить его как сопровождение DnD
            String s = treeCell.getTreeItem().getValue().getName();
            Text t = new Text(s);
            WritableImage image = t.snapshot(null, null);

            db.setDragViewOffsetY(25.0f);
            db.setDragView(image);
        }

        e.consume();
    }

    /**
     * Обработка события OnDragOver
     */
    private void createOnDragOver(DragEvent e, TreeCell<ProductGroup> treeCell){
        Dragboard db = e.getDragboard();
        TreeItem<ProductGroup> target = treeCell.getTreeItem();
        if(db.hasString()) {
            treeView.getSelectionModel().select(target);
            expandIfNeeded(target);
            if (pastePossible(target, db.getString())) {
                e.acceptTransferModes(TransferMode.MOVE);
            } else {
                e.acceptTransferModes(TransferMode.NONE);
            }
        }
        e.consume();
    }

    /**
     * Обработка события OnDragDropped
     */
    private void createOnDragDropped(DragEvent event, TreeCell<ProductGroup> treeCell){
        Dragboard db = event.getDragboard();
        if(db.hasString() && db.getString().contains("pik!")) {
            if (event.getTransferMode().equals(TransferMode.MOVE)) {
                pasteItems(db.getString());
            }
        }
        event.consume();
    }

    private void expandIfNeeded(TreeItem<ProductGroup> targetItem){
        //Избавляемся от NullPointerException
        if (targetItem != null) {
            //Если выделенный targetItem не совпадает с предыдущим
            if (this.hoveredItem == null) {
                this.hoveredItem = targetItem;
                //создаем поток
                hoveredThread = new Thread(() -> {
                    try {
                        //поток засыпает и после сна раскрывает узел
                        Thread.sleep(1000);
                        hoveredItem.setExpanded(true);
                        hoveredThread.join();
                    } catch (InterruptedException e) {
                    }
                });
                hoveredThread.start();
            }else{
                //Если выделен новый узел, то старое значение узла обнуляем,
                // имеющийся поток прерываем и запускаем новый поток
                if(this.hoveredItem != targetItem){
                    hoveredThread.interrupt();
                    hoveredItem = null;
                    expandIfNeeded(targetItem);
                }
            }
        }
    }



    /**
     * Управление с помощью клавиатуры
     * @param treeView Item_TreeView<Item, ProductGroup>
     */
    private void setOnKeyManipulator(Item_TreeView<Item, ProductGroup> treeView) {
        treeView.setOnKeyPressed(e->{

            if (e.getCode() == KeyCode.DELETE) {
                treeView.getItemCommands().delete(e, treeView.getAllSelectedItems()); //DELETE удаляем
            }

            if ((e.getCode() == KeyCode.C && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isControlDown())) {
                String str = cutItems(e); //(CTRL + C) вырезаем
                ClipboardUtils.copyToClipboardText(str);
            }

            if ((e.getCode() == KeyCode.V && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isShiftDown())) {
                String str = ClipboardUtils.getStringFromClipboard();
                if(pastePossible(null, str)) pasteItems(str); //(CTRL + V) вставляем
            }
        });
    }


    public String cutItems(Event e) {
        StringBuilder sb = new StringBuilder();
        sb.append("pik! ");
        ProductGroup selectedItem = treeView.getSelectionModel().getSelectedItem().getValue();
        sb.append("PG");
        sb.append("#");
        sb.append(selectedItem.getId());
        sb.append(" ");

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
    public boolean pastePossible(TreeItem<ProductGroup> targetItem, String str){
        if(targetItem != null )
            targetItem = treeView.getSelectionModel().getSelectedItem();
        if(targetItem == null) targetItem = treeView.getRoot();
        List<ProductGroup> children = treeView.findAllGroupChildren(targetItem);
        //Флаг проверки первого PG в буфере обмена
        boolean pgPK = false;
        //1)ClipboardUtils = null или 2)Начинается НЕ с "pik!"
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
                TreeItem<ProductGroup> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if(selectedItem == null)
                    selectedPG = treeView.getRoot().getValue();
                else
                    selectedPG = selectedItem.getValue();
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

    public void pasteItems(String str) {

        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);

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

    }

}
