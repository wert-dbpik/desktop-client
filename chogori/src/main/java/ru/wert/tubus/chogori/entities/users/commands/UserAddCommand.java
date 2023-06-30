package ru.wert.tubus.chogori.entities.users.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.users.User_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class UserAddCommand implements ICommand {

    private User newItem;
    private User_TableView tableView;

    /**
     *
     * @param tableView User_TableView
     */
    public UserAddCommand(User newItem, User_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            ChogoriServices.CH_USERS.save(newItem);

//            tableView.updateView();
            Platform.runLater(()->{
                tableView.easyUpdate(ChogoriServices.CH_USERS);
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
