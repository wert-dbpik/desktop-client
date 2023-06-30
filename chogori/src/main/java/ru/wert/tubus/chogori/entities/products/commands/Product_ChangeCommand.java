package ru.wert.tubus.chogori.entities.products.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.chogori.entities.products.Product_TableView;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Product_ChangeCommand implements ICommand {

    private Product item;
    private Product_TableView tableView;

    /**
     *
     * @param tableView Product_TableView
     */
    public Product_ChangeCommand(Product item, Product_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            ChogoriServices.CH_QUICK_PRODUCTS.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(ChogoriServices.CH_QUICK_PRODUCTS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });

            log.info("Изменение изделия {} исп. {}", item.getPassport().toUsefulString(), item.getVariant());
            AppStatic.createLog(false, String.format("Изменил изделие '%s' исп.'%s'", item.getPassport().toUsefulString(), item.getVariant()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При зменении изделия {} исп. {} произошла ошибка {} по причине {}",
                    item.getPassport().toUsefulString(), item.getVariant(), e.getMessage(), e.getCause());
        }

    }
}
