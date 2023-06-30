package ru.wert.tubus.chogori.entities.folders.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.chogori.entities.folders.Folder_TableView;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

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
//        BXPrefix.LAST_PREFIX = AppStatic.getPrefixInDecNumber(newItem.getName()); //????
        //Сохраняем новое изделие
        Folder folder = ChogoriServices.CH_QUICK_FOLDERS.save(newItem);

        if (folder != null) { //Если сохранение произошло
            log.info("Добавлен комплект {} ", folder.toUsefulString());
            AppStatic.createLog(false, String.format("Добавил комплект '%s'", folder.toUsefulString()));
        } else {//Если сохранение НЕ произошло
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении изделия {} произошла ошибка", newItem.toUsefulString());

        }

        Platform.runLater(()->{
            tableView.updateVisibleLeafOfTableView(tableView.getUpwardRow().getValue());
            tableView.scrollTo(newItem);
            tableView.getSelectionModel().select(newItem);
        });


    }


}
