package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.products.Product_TableView;
import ru.wert.datapik.utils.entities.products.commands._Product_Commands;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.List;

public class _ProductGroup_Commands<P extends Item> implements ItemCommands<ProductGroup> {

    private IFormView<P> tableView = null; //Таблица каталога, которая обновляется вместе с деревом
    private final ProductGroup_TreeView<P> treeView;
    private final ItemService<P>  dependedItemService;
    private final ItemTableView<Item> dependedTableView;
//    private boolean changesInDependedTableView;

    /**
     * Конструктор для Таблицы
     * @param treeView ProductGroup_TreeView<P>
     * @param tableView IFormView<P>
     * @param dependedItemService ItemService<P>
     */
    public _ProductGroup_Commands(ProductGroup_TreeView<P> treeView, IFormView<P> tableView, ItemService<P> dependedItemService, ItemTableView<Item> dependedTableView) {
        this.treeView = treeView;
        this.tableView = tableView;
        this.dependedItemService = dependedItemService;
        this.dependedTableView = dependedTableView;
//        this.changesInDependedTableView = changesInDependedTableView;

        System.out.println("TableVie = " + tableView + "; DependedTableView = " + dependedTableView);

    }

//    /**
//     * Конструктор для дерева
//     * @param treeView ProductGroup_TreeView<P>
//     * @param dependedItemService ItemService<P>
//     */
//    public _ProductGroup_Commands(ProductGroup_TreeView<P> treeView, ItemService<P> dependedItemService, ItemTableView<Item> dependedTableView) {
//        this.treeView = treeView;
//        this.dependedItemService = dependedItemService;
//        this.dependedTableView = dependedTableView;
//    }

    @Override
    public void add(Event event, ProductGroup newItem){
        ICommand command = new ProductGroup_AddCommand<P>(this, newItem);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<ProductGroup> items){
        ICommand command = new ProductGroup_DeleteCommand<P>(this, items, treeView, tableView, (GroupedItemService<P>) dependedItemService);
        command.execute();
    }

    @Override
    public void change(Event event, ProductGroup item){
        ICommand command = new ProductGroup_ChangeCommand<P>(this, item);
        command.execute();
    }

    public void addProductToFolder(Event event){
        Product_TableView formView = (Product_TableView) treeView.getConnectedForm();
        _Product_Commands commands = (_Product_Commands) formView.getCommands();
        String itemACCRes = formView.getAccWindowRes();

        new FormViewACCWindow<Product>().create(EOperation.ADD, formView, commands, itemACCRes);
    }

    /**
     * Обновление форм после удаления групп
     * @param selectGroup TreeItem<ProductGroup>
     * @param rowToBeSelectedAfterAdding Integer
     */
    public void updateFormsWhenDeleted(TreeItem<ProductGroup> selectGroup, Integer rowToBeSelectedAfterAdding){

        Platform.runLater(() -> {
            treeView.updateView();

            if (tableView == null) {
                treeView.getSelectionModel().select(selectGroup);
                treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
                if(dependedTableView != null) dependedTableView.updateView();
            } else {
                TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
                int row = treeView.getFocusModel().getFocusedIndex();
                treeView.getFocusModel().focus(row);
                treeView.scrollTo(row);

                ((CatalogableTable<? extends CatalogGroup>) tableView).updateOnlyTableView(selectedTreeItemInTable.getValue());
                if(rowToBeSelectedAfterAdding != null) {
                    ((ItemTableView<? extends Item>) tableView).getSelectionModel().select(rowToBeSelectedAfterAdding);
                    ((ItemTableView<? extends Item>) tableView).scrollTo(rowToBeSelectedAfterAdding);
                }
            }

        });
    }

    /**
     * Обновление форм после добавления или изменения группы
     * @param item ProductGroup
     */
    public void updateFormsWhenAddedOrChanged(ProductGroup item) {
        Platform.runLater(()->{
            treeView.updateView();
            if(tableView == null){
                selectTreeViewItem(treeView.findTreeItemById(item.getId()));
                if(dependedTableView != null) dependedTableView.updateView();
            } else {
                TreeItem<? extends CatalogGroup> selectedTreeItemInTable = ((CatalogableTable<? extends CatalogGroup>) tableView).getSelectedTreeItem();
                ((CatalogableTable<? extends CatalogGroup>) tableView).updateOnlyTableView(selectedTreeItemInTable.getValue());
                focusTreeViewItem(treeView.getFocusModel().getFocusedIndex());
                int row = (((ItemTableView<? extends Item>) tableView).getItems()).indexOf(item);
                selectTableViewPos(row);
            }
        });
    }

    /**
     * Выделяет группу TreeItem после операции
     * @param treeItemToBeSelected TreeItem<ProductGroup>
     */
    private void selectTreeViewItem(TreeItem<ProductGroup> treeItemToBeSelected){
        treeView.getSelectionModel().select(treeItemToBeSelected);
        treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
    }

    /**
     * Выделяет фокусом группу TreeItem после операции
     * @param treeItemToBeFocused TreeItem<ProductGroup>
     */
    private void focusTreeViewItem(int treeItemToBeFocused){
        treeView.getFocusModel().focus(treeItemToBeFocused);
        treeView.scrollTo(treeItemToBeFocused);
    }

    /**
     * Выделяет строку в таблице после опреации
     * @param rowToBeSelectedAfterAdding Integer
     */
    private void selectTableViewPos(int rowToBeSelectedAfterAdding){
        ((ItemTableView<? extends Item>) tableView).getSelectionModel().select(rowToBeSelectedAfterAdding);
        ((ItemTableView<? extends Item>) tableView).scrollTo(rowToBeSelectedAfterAdding);
    }
}
