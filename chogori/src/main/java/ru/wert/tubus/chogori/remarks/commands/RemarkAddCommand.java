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
public class RemarkAddCommand implements ICommand {

    private Remark newItem;
    private Remark_TableView tableView;

    /**
     *
     * @param tableView Remark_TableView
     */
    public RemarkAddCommand(Remark newItem, Remark_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        Remark remark = null;
        try {
            remark = CH_QUICK_REMARKS.save(newItem);

            Remark finalRemark = remark;
            Platform.runLater(()->{
                tableView.updateTableView();
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);
                log.info("Добавлен комментарий {} для {}", finalRemark.getId(), finalRemark.getPassport().toUsefulString());
                AppStatic.createLog(true, String.format("Добавил комментарий '%s' для '%s'", finalRemark.getId(), finalRemark.getPassport().toUsefulString()));
            });

            AppStatic.updateTables();

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавление комментария {} для {} произошла ошибка {}", remark.getId(), remark.getPassport().toUsefulString(), e.getMessage());
        }

    }
}
