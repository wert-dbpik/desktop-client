package ru.wert.tubus.chogori.entities.prefixes.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.prefixes.Prefix_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_PREFIXES;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class PrefixDeleteCommand implements ICommand {

    private List<Prefix> items;
    private Prefix_TableView tableView;

    /**
     *
     * @param tableView Prefix_TableView
     */
    public PrefixDeleteCommand(List<Prefix> items, Prefix_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Prefix item : items){
            try {
                CH_PREFIXES.delete(item);
                log.info("Удален префикс {}", item.getName());
                AppStatic.createLog(true, String.format("Удалил префикс '%s'", item.getName()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении префикса {} произошла ошибка {}", item.getName(), e.getMessage());

            }
        }

        tableView.easyUpdate(CH_PREFIXES);

        tableView.getSelectionModel().select(row);

    }
}
