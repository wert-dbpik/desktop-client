package ru.wert.datapik.utils.entities.products.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.utils.entities.products.Product_TableView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PRODUCTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

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
            CH_QUICK_PRODUCTS.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_QUICK_PRODUCTS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });


            log.info("Изменение изделия {} исп. {}", item.getPassport().toUsefulString(), item.getVariant());
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При зменении изделия {} исп. {} произошла ошибка {} по причине {}",
                    item.getPassport().toUsefulString(), item.getVariant(), e.getMessage(), e.getCause());
        }

    }
}
