package ru.wert.datapik.utils.entities.product_groups.commands;

import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
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
public class ProductGroup_DeleteCommand implements ICommand {

    private TreeItem<ProductGroup> item;
    private ProductGroup_TreeView<ProductGroup> treeView;

    private List<ProductGroup> productGroups, notDeletedProductGroups;
    private List<Product> products, notDeletedProducts;


    /**
     *
     * @param treeView ProductGroup_TreeView
     */
    public ProductGroup_DeleteCommand(TreeItem<ProductGroup> item, ProductGroup_TreeView<ProductGroup> treeView) {
        this.item = item;
        this.treeView = treeView;

        productGroups = new ArrayList<>();
        products = new ArrayList<>();

        notDeletedProducts = new ArrayList<>();
        notDeletedProductGroups = new ArrayList<>();
    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = treeView.getRow(item);
            try {

                List<TreeItem<ProductGroup>> allDeletingTreeItems  = treeView.findAllChildren(item);

                //Посчитаем удаляемые папки
                for(TreeItem<ProductGroup> pgItem : allDeletingTreeItems){
                    productGroups.add(pgItem.getValue());
                }
                productGroups.add(item.getValue()); //Добавляем исходную

                //Посчитаем удаляемые изделия
                for(ProductGroup pg : productGroups){
                    products.addAll(CH_QUICK_PRODUCTS.findAllByGroupId(pg.getId()));
                }

                //Произведем удаление
                if(products.isEmpty()){ //Если папки пустые
                    deleteProductGroups();
                } else {
                    boolean answer = Warning2.create($ATTENTION, String.format("Вы уверены, что хотите удалить папку '%s'\n" +
                            "и все входящие в папку изделия?", item.getValue().getName()), "Востановить удаленное будет трудно!");
                    if(answer) {
                        deleteProducts();
                        deleteProductGroups();
                    }

                }

                update(row);

                if(!notDeletedProductGroups.isEmpty() || !notDeletedProducts.isEmpty())
                    Warning1.create($ATTENTION, "Некоторые элементы не удалось удалить!",
                            "Возможно, они имеют зависимости");

            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении группы изделий {} произошла ошибка {} по причине {}",
                        item.getValue().getName(), e.getMessage(), e.getCause());
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
        treeView.updateView();
        treeView.getSelectionModel().select(row);
        treeView.scrollTo(row);
    }
}
