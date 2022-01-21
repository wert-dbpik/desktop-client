package ru.wert.datapik.utils.entities.product_groups.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_ChangeCommand<P extends Item> implements ICommand {

    private ProductGroup item;
    private ProductGroup_TreeView treeView;
    private IFormView<P> tableView;

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

        try {
            boolean res = CH_PRODUCT_GROUPS.update(item);
            if(res){
                log.info("Изменена группа изделий {}", item.getName());
                _ProductGroup_Commands.updateFormsWhenAddedOrChanged(item);

            }
        } catch (Exception e){
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    item.getName(), e.getMessage(), e.getCause());
        }

    }

//    private void updateForms() {
//        Platform.runLater(()->{
//            treeView.updateView();
//            if(tableView == null){
//                _ProductGroup_Commands.selectTreeViewItem(treeView.findTreeItemById(item.getId()));
//            } else {
//                TreeItem<? extends CatalogGroup> selectedTreeItemInTable = ((CatalogableTable<? extends CatalogGroup>) tableView).getSelectedTreeItem();
//                ((CatalogableTable<? extends CatalogGroup>) tableView).updateOnlyTableView(selectedTreeItemInTable.getValue());
//                _ProductGroup_Commands.focusTreeViewItem(treeView.getFocusModel().getFocusedIndex());
//                int row = (((ItemTableView<P>) tableView).getItems()).indexOf(item);
//                _ProductGroup_Commands.selectTableViewPos(row);
//            }
//        });
//    }
}
