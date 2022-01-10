package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.event.Event;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.interfaces.IFormView;
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
    private final ProductGroup_TreeView treeView;

    public _ProductGroup_Commands(ProductGroup_TreeView treeView, IFormView<P> tableView) {
        this.treeView = treeView;
        this.tableView = tableView;
    }

    public _ProductGroup_Commands(ProductGroup_TreeView treeView) {
        this.treeView = treeView;
    }

    @Override
    public void add(Event event, ProductGroup newItem){
        ICommand command = new ProductGroup_AddCommand(newItem, treeView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<ProductGroup> items){
        TreeItem<ProductGroup> selectedTreeItem = treeView.findTreeItemById(items.get(0).getId());
        ICommand command = new ProductGroup_DeleteCommand(selectedTreeItem, treeView);
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
}
