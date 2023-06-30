package ru.wert.tubus.chogori.entities.passports.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.passports.Passport_TableView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Passport_DeleteCommand implements ICommand {

    private List<Passport> items;
    private Passport_TableView tableView;

    /**
     *
     * @param tableView PassportTableView
     */
    public Passport_DeleteCommand(List<Passport> items, Passport_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Passport item : items){
            try {
                ChogoriServices.CH_QUICK_PASSPORTS.delete(item);
                log.info("Удален пользователь {}", item.getName());
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При добавление пользователя {} произошла ошибка {} по причине {}", item.getName(), e.getMessage(), e.getCause());
            }
        }

        tableView.easyUpdate(ChogoriServices.CH_QUICK_PASSPORTS);

        tableView.getSelectionModel().select(row);

    }
}
