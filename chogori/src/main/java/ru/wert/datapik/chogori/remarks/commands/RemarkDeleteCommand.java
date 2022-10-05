package ru.wert.datapik.chogori.remarks.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.remarks.Remark_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.chogori.services.ChogoriServices.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class RemarkDeleteCommand implements ICommand {

    private List<Remark> items;
    private Remark_TableView tableView;

    /**
     *
     * @param tableView Remark_TableView
     */
    public RemarkDeleteCommand(List<Remark> items, Remark_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Remark item : items){
            try {
                CH_REMARKS.delete(item);
                log.info("Удален комментарий {}", item.getName());
                AppStatic.createLog(true, String.format("Удалил комментарий '%s' для '%s'", item.getId(), item.getPassport().toUsefulString()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении комментария {} для {} произошла ошибка {}", item.getId(), item.getPassport().toUsefulString(), e.getMessage());
            }
        }

        tableView.updateTableView();

        tableView.getSelectionModel().select(row);

    }
}
