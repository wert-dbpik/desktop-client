package ru.wert.tubus.chogori.remarks.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Remark;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.remarks.Remark_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_QUICK_REMARKS;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_REMARKS;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class RemarkChangeCommand implements ICommand {

    private Remark item;
    private Remark_TableView tableView;

    /**
     *
     * @param tableView Remark_TableView
     */
    public RemarkChangeCommand(Remark item, Remark_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {


        try {
            CH_QUICK_REMARKS.update(item);

            Platform.runLater(() -> {
                tableView.updateTableView();
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });

            log.info("Обновлен комментарий {} для {}", item.getId(), item.getPassport().toUsefulString());
            AppStatic.createLog(true, String.format("Изменил пользователя '%s'", item.getName()));

            AppStatic.updateTables();
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении комментария {} для {} произошла ошибка {}", item.getId(), item.getPassport().toUsefulString(), e.getMessage());
        }

    }
}
