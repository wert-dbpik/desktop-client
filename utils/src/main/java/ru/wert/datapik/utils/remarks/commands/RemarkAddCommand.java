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

        try {
            CH_REMARKS.save(newItem);

//            tableView.updateView();
            Platform.runLater(()->{
                tableView.easyUpdate(CH_REMARKS);
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);
                log.info("Добавлен пользователь {}", newItem.getName());
                AppStatic.createLog(true, String.format("Добавил пользователя '%s'", newItem.getName()));
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавление пользователя {} произошла ошибка {} по причине {}", newItem.getName(), e.getMessage(), e.getCause());
        }

    }
}
