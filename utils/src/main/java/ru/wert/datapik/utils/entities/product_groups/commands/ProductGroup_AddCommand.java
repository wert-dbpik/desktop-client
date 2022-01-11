package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_AddCommand<P extends Item> implements ICommand {

    private final ProductGroup newItem;
    private final ProductGroup_TreeView<ProductGroup> treeView;
    private IFormView<P> tableView = null;

    /**
     * @param treeView ProductGroupTableView
     */
    public ProductGroup_AddCommand(ProductGroup newItem, ProductGroup_TreeView<ProductGroup> treeView, IFormView<P> tableView) {
        this.newItem = newItem;
        this.treeView = treeView;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        TreeItem<ProductGroup> selectedItem;
        if(tableView == null) {
            selectedItem = treeView.getSelectionModel().getSelectedItem();
            if(selectedItem != null) selectedItem.setExpanded(true);//Условие когда добавляют в корень
        } else {
            selectedItem = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
        }

        try {
            CH_PRODUCT_GROUPS.save(newItem);
            log.info("Добавлена группа изделий {}", newItem.getName());
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    newItem.getName(), e.getMessage(), e.getCause());
        }

        //Обновляем каталог
        Platform.runLater(() -> {
            int row = treeView.getFocusModel().getFocusedIndex();
            treeView.updateView();
            treeView.getSelectionModel().select(row);
            treeView.scrollTo(row);
            if (tableView != null) {
                int trow = ((ItemTableView<?>) tableView).getSelectionModel().getSelectedIndex();
                ((CatalogableTable<ProductGroup>) tableView).setSelectedTreeItem(selectedItem);
                ((ItemTableView<?>) tableView).updateTableView();
                ((ItemTableView<?>) tableView).getSelectionModel().select(trow);
                ((ItemTableView<?>) tableView).scrollTo(trow);
            }

        });


    }
}
