package ru.wert.tubus.chogori.entities.drafts.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.statics.WinformStatic;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.Collections;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

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
            ChogoriServices.CH_QUICK_DRAFTS.update(item);
            tableView.updateRoutineTableView(Collections.singletonList(item), true);
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
