package ru.wert.datapik.chogori.entities.folders.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Folder_ChangeCommand implements ICommand {

    private Folder item;
    private Folder_TableView tableView;

    /**
     *
     * @param tableView Folder_TableView
     */
    public Folder_ChangeCommand(Folder item, Folder_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_QUICK_FOLDERS.update(item);

            Platform.runLater(()->{
                Platform.runLater(()->{
                    tableView.updateVisibleLeafOfTableView(tableView.getUpwardRow().getValue());
                    tableView.scrollTo(item);
                    tableView.getSelectionModel().select(item);
                    log.info("Изменен комплект {} ", item.toUsefulString());
                    AppStatic.createLog(false, String.format("Изменил комплект '%s'", item.toUsefulString()));
                });
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При зменении пакета {} произошла ошибка {}",
                    item.toUsefulString(), e.getMessage());
        }

    }
}
