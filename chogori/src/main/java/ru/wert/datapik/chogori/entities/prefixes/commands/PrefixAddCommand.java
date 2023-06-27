package ru.wert.datapik.chogori.entities.prefixes.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.prefixes.Prefix_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PREFIXES;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class PrefixAddCommand implements ICommand {

    private Prefix newItem;
    private Prefix_TableView tableView;

    /**
     *
     * @param tableView Prefix_TableView
     */
    public PrefixAddCommand(Prefix newItem, Prefix_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            Prefix savedPrefix = CH_PREFIXES.save(newItem);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_PREFIXES);
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);
            });


            log.info("Добавлена префикс {}", newItem.getName());
            AppStatic.createLog(true, String.format("Добавил префикс '%s'", newItem.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении префикса {} произошла ошибка {}", newItem.getName(), e.getMessage());
        }

    }
}
