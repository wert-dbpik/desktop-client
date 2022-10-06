package ru.wert.datapik.chogori.entities.passports.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.passports.Passport_TableView;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_PASSPORTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Passport_ChangeCommand implements ICommand {

    private Passport item;
    private Passport_TableView tableView;

    /**
     *
     * @param tableView PassportTableView
     */
    public Passport_ChangeCommand(Passport item, Passport_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_QUICK_PASSPORTS.update(item);



            Platform.runLater(()->{
                tableView.easyUpdate(CH_QUICK_PASSPORTS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);

                log.info("Обновлен пользователь {}", item.getName());
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении пользователя {} произошла ошибка {} по причине {}", item.getName(), e.getMessage(), e.getCause());
        }

    }
}
