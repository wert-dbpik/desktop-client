package ru.wert.tubus.chogori.entities.drafts.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.warnings.Warning2;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.wert.tubus.winform.warnings.WarningMessages.$ATTENTION;

@Slf4j
public class Draft_NullifyCommand implements ICommand {

    private final Draft_TableView tableView;

    /**
     * Аннудировать чертеж
     * @param tableView Draft_TableView
     */
    public Draft_NullifyCommand(Draft_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void execute() {

        Draft item = tableView.getSelectionModel().getSelectedItem();
        List<Draft> list = ChogoriServices.CH_QUICK_DRAFTS.findByPassport(item.getPassport());
        int row = tableView.getItems().lastIndexOf(list.get(0));

        StringBuilder annulledDrafts = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Draft draft = list.get(i);
                annulledDrafts.append(draft.toUsefulString()).append(" ")
                        .append(EDraftType.getDraftTypeById(draft.getDraftType()).getShortName())
                        .append("-").append(draft.getPageNumber());
            if (i < list.size() - 1)
                annulledDrafts.append(";\n");
            else
                annulledDrafts.append(";");
        }

        boolean ok = Warning2.create($ATTENTION, "Будут аннулированы следущие документы:", annulledDrafts.toString());
        if (ok)
            for (Draft draft : list) {
                draft.setStatus(EDraftStatus.ANNULLED.getStatusId());
                draft.setStatusUser(ChogoriSettings.CH_CURRENT_USER);
                draft.setStatusTime(LocalDateTime.now().toString());

                ChogoriServices.CH_QUICK_DRAFTS.update(draft);

                log.info("Аннулирован чертеж {}", item.toUsefulString());
                AppStatic.createLog(false, String.format("Аннулировал чертеж '%s' (%s) в комплекте '%s'",
                        item.getPassport().toUsefulString(),
                                EDraftType.getDraftTypeById(item.getDraftType()).getShortName() + "-" + item.getPageNumber(),
                        item.getFolder().toUsefulString()));
            }
        else return;

        tableView.updateRoutineTableView(Collections.singletonList(item), true);
    }

}
