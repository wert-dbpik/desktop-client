package ru.wert.datapik.chogori.entities.passports.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.passports.Passport_TableView;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.services.ChogoriServices.CH_QUICK_PASSPORTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Passport_AddCommand implements ICommand {

    private Passport newItem;
    private Passport_TableView tableView;

    /**
     *
     * @param tableView PassportTableView
     */
    public Passport_AddCommand(Passport newItem, Passport_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_QUICK_PASSPORTS.save(newItem);



            Platform.runLater(()->{
                tableView.easyUpdate(CH_QUICK_PASSPORTS);
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);

                log.info("Добавлен пользователь {}", newItem.getName());
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавление пользователя {} произошла ошибка {} по причине {}", newItem.getName(), e.getMessage(), e.getCause());
        }

    }
}
