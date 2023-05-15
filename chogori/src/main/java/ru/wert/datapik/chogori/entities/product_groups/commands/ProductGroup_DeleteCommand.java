package ru.wert.datapik.chogori.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.*;
import ru.wert.datapik.chogori.common.tableView.CatalogableTable;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class ProductGroup_DeleteCommand<P extends Item> implements ICommand {

    private final _ProductGroup_Commands<P> commands;

    private final List<ProductGroup> items;
    private ProductGroup_TreeView<P> treeView;
    private ItemTableView<Item> tableView = null;

    private List<ProductGroup> groupsToBeDeleted;
    private List<P> foldersToBeDeleted;

    private List<ProductGroup> notDeletedGroups = new ArrayList<>();
    private List<P> notDeletedItems = new ArrayList<>();

    private final GroupedItemService<P> itemService;



    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_DeleteCommand(_ProductGroup_Commands<P> commands, List<ProductGroup> items, ProductGroup_TreeView<P> treeView, ItemTableView<Item> tableView, GroupedItemService<P> itemService) {
        this.commands = commands;
        this.items = items;
        this.treeView = treeView;
        this.tableView = tableView;
        this.itemService = itemService;

    }

    @Override
    public void execute() {
        if (items.isEmpty()) return;

        //TODO: Находим элементы, выделяемые после завершения операции

        Integer rowToBeSelectedAfterDeleting = null;
        if (tableView != null) { //Если удаляется узел дерева
            rowToBeSelectedAfterDeleting = findRowToBeSelectedAfterDeleting();
        }

        //TODO:Находим входящие группы
        groupsToBeDeleted = findAllGroupsToBeDeleted(items);
        if (groupsToBeDeleted == null) return;

        //TODO: Находим элементы входящие в группы
        foldersToBeDeleted = findAllFoldersToBeDeleted(groupsToBeDeleted);

        if (!foldersEmpty((List<Folder>) foldersToBeDeleted)) {
            Warning1.create($ATTENTION, "Некоторые комплекты содержат чертежи!",
                    "Комплекты с чертежами удалите отдельно.\nОперация остановлена.");
            return;
        }

        //TODO: Спрашиваем пользователя в последний раз
        boolean answer = Warning2.create($ATTENTION, "Вы уверены, что хотите удалить папки\n" +
                "и все входящие комплекты?", "Востановить удаленное будет трудно!");
        if (answer) {

            Integer finalRowToBeSelectedAfterDeleting = rowToBeSelectedAfterDeleting;
            Task<Void> delTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    //TODO: Удаляем сначала комплекты, входящие в папки, если они есть
                    if (!foldersToBeDeleted.isEmpty())
                        deleteFoldersInGroup(foldersToBeDeleted);
                    //TODO: Удаляем и сами папки
                    deleteProductGroups(groupsToBeDeleted);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    //TODO: Обновляем дерево и таблицу
                    commands.updateFormsWhenDeleted(finalRowToBeSelectedAfterDeleting);

                    //TODO: Предупреждаем пользователя, если не все получилось удалить
                    if (!notDeletedGroups.isEmpty() || !notDeletedItems.isEmpty())
                        Warning1.create($ATTENTION, "Некоторые элементы не удалось удалить!",
                                "Возможно, они имеют зависимости");
                }

                @Override
                protected void failed() {
                    super.failed();
                    Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                    log.error("При удалении произошла ошибка");
                }
            };

            Thread t = new Thread(delTask);
            t.setDaemon(true);
            t.start();
        }

    }

    private boolean foldersEmpty(List<Folder> foldersToBeDeleted) {
        for(Folder f : foldersToBeDeleted){
            if(!CH_QUICK_DRAFTS.findAllByFolder(f).isEmpty())
                return false;
        }
        return true;
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
    private List<P> findAllFoldersToBeDeleted(List<ProductGroup> allGroupsToBeDeleted){
        if(allGroupsToBeDeleted.isEmpty()) return null;

        List<P> foundItems = new ArrayList<>();
        for(ProductGroup group : allGroupsToBeDeleted) {
            log.debug(String.format("findAllFoldersToBeDeleted : Находим элементы, входящие в группу '%s'", group.getName()));
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
                AppStatic.createLog(false, String.format("Удалил группу изделий '%s'", pg.getName()));
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
    private List<P> deleteFoldersInGroup(List<P> allItemsToBeDeleted){
//        List<P> notDeletedItems = new ArrayList<>();
        for(P p : allItemsToBeDeleted){
            List<Draft> draftsInFolder = CH_QUICK_DRAFTS.findAllByFolder((Folder)p);
            if(!draftsInFolder.isEmpty()){
                notDeletedItems.add(p);
                Warning1.create($ATTENTION, String.format("Комплект %s не пустой и не может быть удален!", p.toUsefulString()),
                        "Удалите чертежи из комплекта");
                continue;
            }
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
}
