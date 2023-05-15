package ru.wert.datapik.chogori.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import ru.wert.datapik.chogori.entities.folders.Folder_ACCController;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.chogori.common.interfaces.IFormView;
import ru.wert.datapik.chogori.common.tableView.CatalogableTable;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.List;

public class _ProductGroup_Commands<P extends Item> implements ItemCommands<ProductGroup> {

    private ProductGroup_TreeView<P> treeView;

    public ProductGroup_TreeView<Item> getTreeView() {
        return (ProductGroup_TreeView<Item>) treeView;
    }

    @Getter private ItemTableView<Item> tableView;
    private final ItemService<P> tableItemService;

    private final CatalogableTable<? extends CatalogGroup> catTableView;


    /**
     * Конструктор
     * @param treeView ProductGroup_TreeView<P>
     * @param tableView ItemTableView<Item>
     * @param tableItemService ItemService<P>, ItemService для TableView
     */
    public _ProductGroup_Commands(ProductGroup_TreeView<P> treeView, ItemTableView<Item> tableView, ItemService<P> tableItemService) {
        this.treeView = treeView;
        this.tableView = tableView;
        this.tableItemService = tableItemService;


        //Для удобства и сокращения длины строк
        this.catTableView = (CatalogableTable<? extends CatalogGroup>) tableView;

    }

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
        ICommand command = new ProductGroup_DeleteCommand<P>(this, items, treeView, tableView, (GroupedItemService<P>) tableItemService);
        command.execute();
    }

    @Override
    public void change(Event event, ProductGroup item){
        ICommand command = new ProductGroup_ChangeCommand<P>(this, item);
        command.execute();
    }

    public void addProductToFolder(Event event){
        ItemTableView<Item> foldersTableViewView = treeView.getConnectedForm();
        ItemCommands<?> commands = ((Folder_TableView)foldersTableViewView).getCommands();

        String itemACCRes = ((Folder_TableView)foldersTableViewView).getAccWindowRes();

        new FormViewACCWindow<Folder>().create(EOperation.ADD, (IFormView)foldersTableViewView, (ItemCommands<Folder>) commands, itemACCRes, treeView);

    }

    /**
     * Обновление форм после удаления групп
     * @param rowToBeSelectedAfterDeleting Integer
     */
    public void updateFormsWhenDeleted(Integer rowToBeSelectedAfterDeleting) {
        TreeItem<? extends CatalogGroup> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();

        treeView.updateView();
        treeView.getFocusModel().focus(selectedItemIndex - 1);

        if (catTableView != null) {
            TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getUpwardRow();
            if (selectedTreeItemInTree != null && selectedTreeItemInTable.getValue().equals(selectedTreeItemInTree.getValue())) {
                selectedTreeItemInTable = selectedTreeItemInTable.getParent();
            }
            catTableView.updateVisibleLeafOfTableView(selectedTreeItemInTable.getValue());
            if (rowToBeSelectedAfterDeleting != null) {
                tableView.getSelectionModel().select(rowToBeSelectedAfterDeleting);
            }
        }
    }
}
