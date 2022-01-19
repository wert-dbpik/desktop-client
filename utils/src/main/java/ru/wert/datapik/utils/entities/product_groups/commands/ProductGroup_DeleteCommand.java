package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.*;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PRODUCTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_DeleteCommand<P extends Item> implements ICommand {

    private final List<ProductGroup> items;
    private ProductGroup_TreeView<ProductGroup> treeView;
    private IFormView<P> tableView = null;

    private List<ProductGroup> groupsToBeDeleted, notDeletedGroups;
    private List<P> itemsToBeDeleted, notDeletedItems;

    private final GroupedItemService<P> itemService;



    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_DeleteCommand(List<ProductGroup> items, ProductGroup_TreeView<ProductGroup> treeView, IFormView<P> tableView, ItemService<P> itemService) {
        this.items = items;
        this.treeView = treeView;
        this.tableView = tableView;
        this.itemService = (GroupedItemService<P>) itemService;
    }

    @Override
    public void execute() {

        if(items.isEmpty()) return;
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
//        int row = treeView.getRow(item);
        TreeItem<ProductGroup> itemToBeSelectedAfterDeleting = null;
        Integer rowToBeSelectedAfterDeleting = null;
        if (tableView == null) {
            //Находим родителя узла
            itemToBeSelectedAfterDeleting = treeView.findTreeItemById(items.get(0).getId()).getParent();
        } else {
            rowToBeSelectedAfterDeleting = findRowToBeSelectedAfterDeleting();
        }

        try {
            groupsToBeDeleted = findAllGroupsToBeDeleted(items);
            if (groupsToBeDeleted == null) return;
            itemsToBeDeleted = findAllItemsToBeDeleted(groupsToBeDeleted);

            if (itemsToBeDeleted != null && !itemsToBeDeleted.isEmpty()) {
                notDeletedItems = deleteItemsInGroup(itemsToBeDeleted);
            }


            boolean answer = Warning2.create($ATTENTION, "Вы уверены, что хотите удалить папки\n" +
                    "и все входящие в папку изделия?", "Востановить удаленное будет трудно!");
            if (answer) {

                deleteItemsInGroup(itemsToBeDeleted);

                deleteProductGroups(groupsToBeDeleted);
            }


            _ProductGroup_Commands.update(itemToBeSelectedAfterDeleting, rowToBeSelectedAfterDeleting);

            if (!notDeletedGroups.isEmpty() || !notDeletedItems.isEmpty())
                Warning1.create($ATTENTION, "Некоторые элементы не удалось удалить!",
                        "Возможно, они имеют зависимости");

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
            log.error("При удалении произошла ошибка");
        }

    }

    private Integer findRowToBeSelectedAfterDeleting(){
        log.debug("findRowToBeSelectedAfterDeleting : Определяем строку выделяемую после удаления");
        Integer selectedRowAfterDeleting = -1;
        List<Integer> selectedRows = ((ItemTableView<Item>) tableView).getSelectionModel().getSelectedIndices();
        int firstSelectedRow = selectedRows.get(0);
        int lastSelectedRow = selectedRows.get(selectedRows.size() - 1);//строка перед первой выделенной строкой
        selectedRowAfterDeleting = firstSelectedRow - 1; //последняя строка после выделения
        if (selectedRowAfterDeleting == 0) { //Если выбрана первая строка
            selectedRowAfterDeleting = lastSelectedRow + 1;
            //Если выходит за пределы таблицы
            if (selectedRowAfterDeleting >= ((ItemTableView<Item>) tableView).getItems().size())
                selectedRowAfterDeleting = null;
        }

        return selectedRowAfterDeleting;
    }

    /**
     * Находим подгруппы, входящие в выделенные группы
     * @param selectedGroups List<ProductGroup>, группы выделенные пользователем для удаления
     * @return List<ProductGroup>
     */
    private List<ProductGroup> findAllGroupsToBeDeleted(List<ProductGroup> selectedGroups){
        if (selectedGroups.isEmpty()) return null;
        List<ProductGroup> foundGroups = new ArrayList<>();

        log.debug("findAllGroupsToBeDeleted : Определяем группы, входящие в выделенные группы");
        for (ProductGroup selectedGroup : selectedGroups) {
            log.debug(String.format("Находим TreeItem для выделенной группы '%s'", selectedGroup.getName()));
            TreeItem<ProductGroup> selectedTreeItem = treeView.findTreeItemById(selectedGroup.getId());

            log.debug(String.format("findAllGroupsToBeDeleted : Находим для TreeItem '%s' все входящие узлы", selectedTreeItem.getValue().getName()));
            foundGroups.add(selectedTreeItem.getValue());
            List<TreeItem<ProductGroup>> treeItemsInSelectedTreeItem = treeView.findAllChildren(selectedTreeItem);
            for (TreeItem<ProductGroup> treeItem : treeItemsInSelectedTreeItem) {
                foundGroups.add(treeItem.getValue());
            }
        }

        return foundGroups;

    }

    /**
     * Находим все элементы, входящие в найденные ранее группы на удаление
     * @param allGroupsToBeDeleted List<ProductGroup> найденные ранее группы на удаление
     * @return List<P>
     */
    private List<P> findAllItemsToBeDeleted(List<ProductGroup> allGroupsToBeDeleted){
        if(allGroupsToBeDeleted.isEmpty()) return null;

        List<P> foundItems = new ArrayList<>();
        for(ProductGroup group : allGroupsToBeDeleted) {
            log.debug(String.format("findAllItemsToBeDeleted : Находим элементы, входящие в группу '%s'", group.getName()));
            foundItems.addAll(itemService.findAllByGroupId(group.getId()));
        }

        return foundItems;
    }

    /**
     * Удаляем группы
     * @param allGroupsToBeDeleted List<ProductGroup>
     * @return List<ProductGroup> notDeletedProductGroups
     */
    private List<ProductGroup> deleteProductGroups(List<ProductGroup> allGroupsToBeDeleted) {
        List<ProductGroup> notDeletedProductGroups = new ArrayList<>();
        for(ProductGroup pg : allGroupsToBeDeleted){
            boolean res = CH_PRODUCT_GROUPS.delete(pg);
            if(res){
                log.info("Удалена группа изделий {}", pg.getName());
            } else {
                log.info("Не удалось удалить группу изделий {}", pg.getName());
                notDeletedProductGroups.add(pg);
            }
        }

        return notDeletedProductGroups;
    }

    /**
     * Удаляем элементы, входящие в группы
     * @param allItemsToBeDeleted List<P>
     * @return List<P> notDeletedItems
     */
    private List<P> deleteItemsInGroup(List<P> allItemsToBeDeleted){
        List<P> notDeletedItems = new ArrayList<>();
        for(P p : allItemsToBeDeleted){
            boolean res = itemService.delete(p);
            if(res){
                log.info("Удалено изделие {}", p.toUsefulString());
            } else {
                log.info("Не удалось удалить изделие {}", p.getName());
                notDeletedItems.add(p);
            }
        }
        return notDeletedItems;
    }

    /**
     *
     * @param row int
     */
    private void update(int row) {

        Platform.runLater(() -> {
            TreeItem<ProductGroup> selectedTreeItemInTable = ((CatalogableTable<ProductGroup>) tableView).getSelectedTreeItem();
            treeView.updateView();
            if (tableView == null) {
                treeView.getSelectionModel().select(row);
                treeView.scrollTo(row);
            } else {
                treeView.getFocusModel().focus(row);
                treeView.scrollTo(row);
                int trow = ((ItemTableView<P>) tableView).getSelectionModel().getSelectedIndex();
                ((CatalogableTable<? extends CatalogGroup>) tableView).updateOnlyTableView(selectedTreeItemInTable.getValue());
                ((ItemTableView<P>) tableView).getSelectionModel().select(trow);
                ((ItemTableView<P>) tableView).scrollTo(trow);
            }

        });

    }
}
