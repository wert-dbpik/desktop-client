package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.List;

public class _ProductGroup_Commands<P extends Item> implements ItemCommands<ProductGroup> {

//    private IFormView<P> tableView = null; //Таблица каталога, которая обновляется вместе с деревом
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
        ItemTableView<Item> formView = treeView.getConnectedForm();
        ItemCommands<?> commands = ((Folder_TableView)formView).getCommands();

        String itemACCRes = ((Folder_TableView)formView).getAccWindowRes();

        new FormViewACCWindow<Folder>().create(EOperation.ADD, (IFormView)formView, (ItemCommands<Folder>) commands, itemACCRes);
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
                TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getUpwardTreeItemRow();
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

//    /**
//     * Обновление форм после добавления или изменения группы
//     * @param item ProductGroup
//     */
//    public void updateFormsWhenAddedOrChanged(ProductGroup item) {
//        TreeItem<? extends CatalogGroup> selectedTreeItemInTree = treeView.getSelectionModel().getSelectedItem();
//        int selectedItemIndex = treeView.getSelectionModel().getSelectedIndex();
//        //Заранее раскрываем выбранный узел
//        //selectedTreeItemInTree != null - условие при добавлении в корень
//        if(selectedTreeItemInTree != null) selectedTreeItemInTree.setExpanded(true);
//
//        Platform.runLater(() -> {
//            treeView.updateView();
//            TreeItem<? extends CatalogGroup> addedTreeItem = treeView.findTreeItemById(item.getId());
//            treeView.getFocusModel().focus(selectedItemIndex + addedTreeItem.getParent().getChildren().indexOf(addedTreeItem) + 1);
//            if (catTableView != null) { //Если имеем дело с каталогом, а не только с деревом
//                catTableView.updateOnlyTableView(catTableView.getSelectedTreeItem().getValue());
//                tableView.getSelectionModel().select(item);
//                tableView.scrollTo(item);
//            }
//        });
//    }
}
