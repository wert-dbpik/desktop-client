package ru.wert.datapik.utils.entities.folders.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.common.components.BXPrefix;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Folder_AddCommand implements ICommand {

    private Folder newItem;
    private Folder_TableView tableView;

    /**
     *
     * @param tableView Folder_TableView
     */
    public Folder_AddCommand(Folder newItem, Folder_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //Сохраняем последний используемый префикс
        BXPrefix.LAST_PREFIX = AppStatic.getPrefixInDecNumber(newItem.getDecNumber());
        //Сохраняем новое изделие
        Folder folder = CH_QUICK_FOLDERS.save(newItem);

        if (folder != null) { //Если сохранение произошло
            log.info("Добавлен пакет {} ", folder.toUsefulString());
        } else {//Если сохранение НЕ произошло
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении изделия {} произошла ошибка", newItem.toUsefulString());

        }

        Platform.runLater(()->{
            tableView.updateTableView();
            tableView.scrollTo(newItem);
            tableView.getSelectionModel().select(newItem);
        });


    }


}
