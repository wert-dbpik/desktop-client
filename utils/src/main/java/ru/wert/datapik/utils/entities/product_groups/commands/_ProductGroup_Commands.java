package ru.wert.datapik.utils.entities.product_groups.commands;

import com.google.gson.internal.bind.util.ISO8601Utils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
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

//    private IFormView<P> tableView = null; //Таблица каталога, которая обновляется вместе с деревом
    private final ProductGroup_TreeView<P> treeView;
    private final ItemService<P> tableItemService;
    private final ItemTableView<Item> tableView;
    private final CatalogableTable<? extends CatalogGroup> catTableView;
    private final Event event;


    /**
     * Конструктор
     * @param treeView ProductGroup_TreeView<P>
     * @param tableView ItemTableView<Item>
     * @param tableItemService ItemService<P>, ItemService для TableView
     * @param event Event, для выяснения источника события
     */
    public _ProductGroup_Commands(ProductGroup_TreeView<P> treeView, ItemTableView<Item> tableView, ItemService<P> tableItemService, Event event) {
        this.treeView = treeView;
        this.tableView = tableView;
        this.tableItemService = tableItemService;
        this.event = event;

        //Для удобства и сокращения длины строк
        this.catTableView = (CatalogableTable<? extends CatalogGroup>) tableView;

    }

    @Override
    public void add(Event event, ProductGroup newItem){
        ICommand command = new ProductGroup_AddCommand<P>(this, newItem, event);
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
        Product_TableView formView = (Product_TableView) treeView.getConnectedForm();
        _Product_Commands commands = (_Product_Commands) formView.getCommands();
        String itemACCRes = formView.getAccWindowRes();

        new FormViewACCWindow<Product>().create(EOperation.ADD, formView, commands, itemACCRes);
    }

    /**
     * Обновление форм после удаления групп
     * @param rowToBeSelectedAfterDeleting Integer
     */
    public void updateFormsWhenDeleted(Integer rowToBeSelectedAfterDeleting){
        TreeItem<? extends CatalogGroup> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();

        Platform.runLater(() -> {
            treeView.updateView();
            treeView.getFocusModel().focus(selectedItemIndex - 1);
            treeView.scrollTo(selectedItemIndex - 1);

            if (catTableView != null) {
                TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
                if(selectedTreeItemInTree != null && selectedTreeItemInTable.getValue().equals(selectedTreeItemInTree.getValue())) {
                    selectedTreeItemInTable = selectedTreeItemInTable.getParent();
                }
                catTableView.updateOnlyTableView(selectedTreeItemInTable.getValue());
                if (rowToBeSelectedAfterDeleting != null) {
                    tableView.getSelectionModel().select(rowToBeSelectedAfterDeleting);
                    tableView.scrollTo(rowToBeSelectedAfterDeleting);
                }
            }

        });

    }

    /**
     * Обновление форм после добавления или изменения группы
     * @param item ProductGroup
     */
    public void updateFormsWhenAddedOrChanged(ProductGroup item) {
        TreeItem<? extends CatalogGroup> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();
        //Заранее раскрываем выбранный узел
        //selectedTreeItemInTree != null - условие при добавлении в корень
        if(selectedTreeItemInTree != null) selectedTreeItemInTree.setExpanded(true);

        Platform.runLater(() -> {
            treeView.updateView();
            TreeItem<? extends CatalogGroup> addedTreeItem = treeView.findTreeItemById(item.getId());
            treeView.getFocusModel().focus(selectedItemIndex + addedTreeItem.getParent().getChildren().indexOf(addedTreeItem) + 1);
            if (catTableView != null) { //Если имеем дело с каталогом, а не только с деревом
                catTableView.updateOnlyTableView(catTableView.getSelectedTreeItem().getValue());
                tableView.getSelectionModel().select(item);
                tableView.scrollTo(item);
            }
        });
    }
}
