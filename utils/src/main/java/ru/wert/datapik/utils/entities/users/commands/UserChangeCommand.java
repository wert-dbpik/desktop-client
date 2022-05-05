package ru.wert.datapik.utils.entities.users.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.users.User_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class UserChangeCommand implements ICommand {

    private User item;
    private User_TableView tableView;

    /**
     *
     * @param tableView User_TableView
     */
    public UserChangeCommand(User item, User_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_USERS.update(item);

            Platform.runLater(() -> {
                tableView.easyUpdate(CH_USERS);
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
