package ru.wert.tubus.chogori.entities.prefixes.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.prefixes.Prefix_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_PREFIXES;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class PrefixChangeCommand implements ICommand {

    private Prefix item;
    private Prefix_TableView tableView;

    /**
     *
     * @param tableView Prefix_TableView
     */
    public PrefixChangeCommand(Prefix item, Prefix_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_PREFIXES.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_PREFIXES);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });


            log.info("Обновлены префиксы {}", item.getName());
            AppStatic.createLog(true, String.format("Изменил префикс '%s'", item.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении префикса {} произошла ошибка {}", item.getName(), e.getMessage());
}

    }
}
