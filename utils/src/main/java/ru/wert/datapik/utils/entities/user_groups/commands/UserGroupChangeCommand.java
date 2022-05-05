package ru.wert.datapik.utils.entities.user_groups.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.user_groups.UserGroup_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USER_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class UserGroupChangeCommand implements ICommand {

    private UserGroup item;
    private UserGroup_TableView tableView;

    /**
     *
     * @param tableView UserGroup_TableView
     */
    public UserGroupChangeCommand(UserGroup item, UserGroup_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_USER_GROUPS.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_USER_GROUPS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);
            });


            log.info("Обновлены разрешения для группы пользователей {}", item.getName());
            AppStatic.createLog(true, String.format("Изменил группу пользователей '%s'", item.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении разрешений для группы пользователей {} произошла ошибка {}", item.getName(), e.getMessage());
}

    }
}
