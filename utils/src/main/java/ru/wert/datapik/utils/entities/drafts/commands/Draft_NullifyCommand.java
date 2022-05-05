package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.time.LocalDateTime;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

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
        List<Draft> list = CH_QUICK_DRAFTS.findByPassport(item.getPassport());
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
                draft.setStatusUser(CH_CURRENT_USER);
                draft.setStatusTime(LocalDateTime.now().toString());

                CH_QUICK_DRAFTS.update(draft);

                log.info("Аннулирован чертеж {}", item.toUsefulString());
                AppStatic.createLog(false, String.format("Аннулировал чертеж '%s' (%s) в комплекте '%s'",
                        item.getPassport().toUsefulString(),
                                EDraftType.getDraftTypeById(item.getDraftType()).getShortName() + "-" + item.getPageNumber(),
                        item.getFolder().toUsefulString()));
            }
        else return;

        Platform.runLater(() -> {
            tableView.updateView();
            tableView.scrollTo(row);
            tableView.getSelectionModel().select(row);
        });
    }

}
