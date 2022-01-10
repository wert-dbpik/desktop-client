package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_ChangeCommand<P extends Item> implements ICommand {

    private ProductGroup item;
    private ProductGroup_TreeView treeView;
    private IFormView<P> tableView = null;

    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_ChangeCommand(ProductGroup item, ProductGroup_TreeView treeView, IFormView<P> tableView) {
        this.item = item;
        this.treeView = treeView;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        int row = treeView.getSelectionModel().getSelectedIndex();

        try {
            CH_PRODUCT_GROUPS.update(item);
            Platform.runLater(()->{
                treeView.updateView();
                treeView.getSelectionModel().select(row);
                treeView.scrollTo(row);
                if(tableView != null){
                    tableView.updateView();
                    treeView.getSelectionModel().select(row);
                    treeView.scrollTo(row);
                }
            });

            log.info("Изменение группы изделий {}", item.getName());
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При зменении группы изделий {} произошла ошибка {} по причине {}",
                    item.getName(), e.getMessage(), e.getCause());
        }

    }
}
