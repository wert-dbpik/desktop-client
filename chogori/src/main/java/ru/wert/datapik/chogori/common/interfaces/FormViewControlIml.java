package ru.wert.datapik.chogori.common.interfaces;

import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.entities.folders.commands.Folder_DeleteCommand;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.chogori.entities.product_groups.commands.ProductGroup_DeleteCommand;
import ru.wert.datapik.chogori.entities.product_groups.commands._ProductGroup_Commands;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FOLDERS;

public class FormViewControlIml<P extends Item> {

    private final Folder_TableView view;
    private final ProductGroup_TreeView<P> treeView;
    private IFormView<P> tableView = null;

    public FormViewControlIml(Folder_TableView view, ProductGroup_TreeView<P> treeView, IFormView<P> tableView) {
        this.view = view;
        this.treeView = treeView;
        this.tableView = tableView;

        view.setOnKeyPressed(this::onKeyPressed);
    }

    public void onKeyPressed(KeyEvent ke){
        if(view.isFocused()) {

            if (ke.getCode() == KeyCode.DELETE) {
                deleteItem(ke); //DELETE удаляем
            }

//            if (ke.getCode() == KeyCode.C && ke.isControlDown()) {
//                eraseRows(ke); //(CTRL + C) вырезаем
//            }
//
//            if (ke.getCode() == KeyCode.V && ke.isControlDown()) {
//                pasteRows(ke, false); //(CTRL + V) вставляем
//            }
//
//            if (ke.getCode() == KeyCode.INSERT && ke.isControlDown()) {
//                eraseRows(ke); //(CTRL + INSERT) вырезаем
//            }
//
//            if (ke.getCode() == KeyCode.INSERT && ke.isShiftDown()) {
//                pasteRows(ke, false); //(SHIFT + INSERT) вставляем
//            }
//
//            if (ke.getCode() == KeyCode.B && ke.isControlDown()) {
//                TreeController tc =  catalogableTableController.getTreeController();
//
//                //Определяем откуда произошло перемещение
//                TreeItem<CatalogGroup> sourceTreeItem = tc.findTreeItemByName(tc.getBackToDraggedSource().get(0));
//                tc.getTreeView().getSelectionModel().select(sourceTreeItem);
//                updateTable(sourceTreeItem, Boolean.valueOf(tc.getBackToDraggedSource().get(1)));
//            }
        }
    }

    private List<Item> getSelectedItems(){
        List<Item> selectedItems = view.getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty()) return null;
        return selectedItems;
    }

    private void deleteItem(KeyEvent ke){
        List<Item> items = getSelectedItems();
        if(items == null) return;

        for(Item item : items){
            if(item instanceof ProductGroup){

                TreeItem<ProductGroup> selectedTreeItem = treeView.findTreeItemById(item.getId());
                _ProductGroup_Commands<P> commands = (_ProductGroup_Commands<P>) treeView.getItemCommands();
                new ProductGroup_DeleteCommand<P>(commands, (List<ProductGroup>) selectedTreeItem, treeView, (ItemTableView<Item>)tableView, (GroupedItemService<P>) CH_FOLDERS).execute();
            }
            if(item instanceof Folder){
                List<Folder> folders = new ArrayList<>();
                folders.add((Folder)item);
                new Folder_DeleteCommand(folders, view).execute();
            }

            view.updateTableView();
        }



    }
}
