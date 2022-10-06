package ru.wert.datapik.chogori.entities.products.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.products.Product_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_PRODUCTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Product_DeleteCommand implements ICommand {

    private List<Product> items;
    private Product_TableView tableView;

    /**
     *
     * @param tableView Product_TableView
     */
    public Product_DeleteCommand(List<Product> items, Product_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Product item : items){
            try {
                CH_QUICK_PRODUCTS.delete(item);
                log.info("Удалено изделие {}", item.toUsefulString());
                AppStatic.createLog(false, String.format("Удалил изделие '%s'", item.getPassport().toUsefulString()));
            } catch (Exception e) {
                e.printStackTrace();
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении изделия {} произошла ошибка", item.toUsefulString());
            }
        }

        tableView.updateCatalogView(tableView.getChosenCatalogItem(), true);

//            tableView.updateView();
        tableView.scrollTo(row);
        tableView.getSelectionModel().select(row);

    }
}
