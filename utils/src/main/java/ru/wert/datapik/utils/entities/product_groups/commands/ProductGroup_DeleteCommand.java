package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
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

    private List<TreeItem<ProductGroup>> items;
    private ProductGroup_TreeView<ProductGroup> treeView;
    private IFormView<P> tableView = null;

    private List<ProductGroup> productGroups, notDeletedProductGroups;
    private List<Product> products, notDeletedProducts;


    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_DeleteCommand(List<TreeItem<ProductGroup>> items, ProductGroup_TreeView<ProductGroup> treeView, IFormView<P> tableView) {
        this.items = items;
        this.treeView = treeView;
        this.tableView = tableView;

        productGroups = new ArrayList<>();
        products = new ArrayList<>();

        notDeletedProducts = new ArrayList<>();
        notDeletedProductGroups = new ArrayList<>();
    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
//        int row = treeView.getRow(item);
        TreeItem<ProductGroup> selectedItemAfterDeleting = null;
        Integer selectedRowAfterDeleting;
        if (tableView == null) {
            selectedItemAfterDeleting = treeView.getSelectionModel().getSelectedItem();
            selectedRowAfterDeleting = null;
        } else {
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
        }
        try {

            List<TreeItem<ProductGroup>> allDeletingTreeItems = treeView.findAllChildren(items);

            //Посчитаем удаляемые папки
            for (TreeItem<ProductGroup> pgItem : allDeletingTreeItems) {
                productGroups.add(pgItem.getValue());
            }
            productGroups.add(items.getValue()); //Добавляем исходную

            //Посчитаем удаляемые изделия
            for (ProductGroup pg : productGroups) {
                products.addAll(CH_QUICK_PRODUCTS.findAllByGroupId(pg.getId()));
            }

            //Произведем удаление
            if (products.isEmpty()) { //Если папки пустые
                deleteProductGroups();
            } else {
                boolean answer = Warning2.create($ATTENTION, String.format("Вы уверены, что хотите удалить папку '%s'\n" +
                        "и все входящие в папку изделия?", items.getValue().getName()), "Востановить удаленное будет трудно!");
                if (answer) {
                    deleteProducts();
                    deleteProductGroups();
                }

            }


            _ProductGroup_Commands.update(selectedItemAfterDeleting, selectedRowAfterDeleting);

            if (!notDeletedProductGroups.isEmpty() || !notDeletedProducts.isEmpty())
                Warning1.create($ATTENTION, "Некоторые элементы не удалось удалить!",
                        "Возможно, они имеют зависимости");

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
            log.error("При удалении группы изделий {} произошла ошибка {} по причине {}",
                    items.getValue().getName(), e.getMessage(), e.getCause());
        }

    }

    private void deleteProductGroups() {
        for(ProductGroup pg : productGroups){
            boolean res = CH_PRODUCT_GROUPS.delete(pg);
            if(res){
                log.info("Удалена группа изделий {}", pg.getName());
            } else {
                log.info("Не удалось удалить группу изделий {}", pg.getName());
                notDeletedProductGroups.add(pg);
            }
        }
    }

    private void deleteProducts(){
        for(Product p : products){
            boolean res = CH_QUICK_PRODUCTS.delete(p);
            if(res){
                log.info("Удалено изделие {}", p.toUsefulString());
            } else {
                log.info("Не удалось удалить изделие {}", p.getName());
                notDeletedProducts.add(p);
            }
        }
    }

    private void update(int row) {
//        Platform.runLater(() -> {
//            treeView.updateView();
//            if (tableView == null) {
//                treeView.getSelectionModel().select(row);
//                treeView.scrollTo(row);
//            } else {
//                int treeRow = treeView.getFocusModel().getFocusedIndex();
//                treeView.getSelectionModel().select(treeRow);
//                treeView.scrollTo(treeRow);
//                int trow = ((TableView<?>) tableView).getSelectionModel().getSelectedIndex();
//                ((ItemTableView<?>) tableView).updateTableView();
//                ((ItemTableView<?>) tableView).getSelectionModel().select(trow);
//                ((ItemTableView<?>) tableView).scrollTo(trow);
//            }
//
//        });

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
