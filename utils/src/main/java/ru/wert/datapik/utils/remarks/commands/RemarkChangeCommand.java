package ru.wert.datapik.utils.remarks.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.remarks.Remark_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_REMARKS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

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
            CH_REMARKS.update(item);

            Platform.runLater(() -> {
                tableView.easyUpdate(CH_REMARKS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });

            log.info("Обновлен пользователь {}", item.getName());
            AppStatic.createLog(true, String.format("Изменил пользователя '%s'", item.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении пользователя {} произошла ошибка {} по причине {}", item.getName(), e.getMessage(), e.getCause());
        }

    }
}
