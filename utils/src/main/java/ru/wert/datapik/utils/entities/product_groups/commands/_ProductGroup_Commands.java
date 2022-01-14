package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
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

public class _ProductGroup_Commands implements ItemCommands<ProductGroup> {

    private static IFormView<? extends Item> tableView = null; //Таблица каталога, которая обновляется вместе с деревом
    private static ProductGroup_TreeView<? extends Item> treeView;

    public _ProductGroup_Commands(ProductGroup_TreeView<? extends Item> treeView, IFormView<? extends Item> tableView) {
        _ProductGroup_Commands.treeView = treeView;
        _ProductGroup_Commands.tableView = tableView;
    }

    public _ProductGroup_Commands(ProductGroup_TreeView treeView) {
        this.treeView = treeView;
    }

    @Override
    public void add(Event event, ProductGroup newItem){
        ICommand command = new ProductGroup_AddCommand(newItem, treeView, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<ProductGroup> items){
//        TreeItem<ProductGroup> selectedTreeItem = treeView.findTreeItemById(items.get(0).getId());
        ICommand command = new ProductGroup_DeleteCommand(items, treeView, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, ProductGroup item){
        ICommand command = new ProductGroup_ChangeCommand(item, treeView, tableView);
        command.execute();
    }

    public void addProductToFolder(Event event){
        Product_TableView formView = (Product_TableView) treeView.getConnectedForm();
        _Product_Commands commands = (_Product_Commands) formView.getCommands();
        String itemACCRes = formView.getAccWindowRes();

        new FormViewACCWindow<Product>().create(EOperation.ADD, formView, commands, itemACCRes);
    }

    /**
     * Обновляет как TreeView так и TableView
     * @param selectGroup
     * @param selectTableRow
     */
    public static void update(TreeItem<ProductGroup> selectGroup, Integer selectTableRow){

        Platform.runLater(() -> {
            treeView.updateView();
            if (tableView == null) {
                treeView.getSelectionModel().select(selectGroup);
                treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
            } else {
                TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
                int row = treeView.getFocusModel().getFocusedIndex();
                treeView.getFocusModel().focus(row);
                treeView.scrollTo(row);

//                int trow = ((ItemTableView<? extends Item>) tableView).getSelectionModel().getSelectedIndex();
                ((CatalogableTable<? extends CatalogGroup>) tableView).updateOnlyTableView(selectedTreeItemInTable.getValue());
                if(selectTableRow != null) {
                    ((ItemTableView<? extends Item>) tableView).getSelectionModel().select(selectTableRow);
                    ((ItemTableView<? extends Item>) tableView).scrollTo(selectTableRow);
                }
            }

        });
    }
}
