package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Draft_ChangeCommand implements ICommand {

    private Draft item;
    private Draft_TableView tableView;

    /**
     *
     * @param tableView Draft_TableView
     */
    public Draft_ChangeCommand(Draft item, Draft_TableView tableView) {
        this.item = item;
        this.tableView = tableView;
    }

    @Override
    public void execute() {

        try {
            CH_QUICK_DRAFTS.update(item);
            tableView.updateDraftTableView(item);
            log.info("Изменение параметров чертежа {}", item.toUsefulString());
            AppStatic.createLog(false, String.format("Изменил чертеж '%s' (%s) в комплекте '%s'",
                    item.getPassport().toUsefulString(),
                            EDraftType.getDraftTypeById(item.getDraftType()).getShortName() + "-" + item.getPageNumber(),
                    item.getFolder().toUsefulString()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При изменении параметров чертежа {} произошла ошибка",
                    item.toUsefulString());
            e.printStackTrace();
        }
    }
}
