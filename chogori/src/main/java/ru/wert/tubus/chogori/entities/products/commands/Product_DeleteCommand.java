package ru.wert.tubus.chogori.entities.products.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.products.Product_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

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
                ChogoriServices.CH_QUICK_PRODUCTS.delete(item);
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
