package ru.wert.datapik.chogori.entities.user_groups.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.user_groups.UserGroup_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.services.ChogoriServices.CH_USER_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class UserGroupAddCommand implements ICommand {

    private UserGroup newItem;
    private UserGroup_TableView tableView;

    /**
     *
     * @param tableView UserGroup_TableView
     */
    public UserGroupAddCommand(UserGroup newItem, UserGroup_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            CH_USER_GROUPS.save(newItem);

            Platform.runLater(()->{
                tableView.easyUpdate(CH_USER_GROUPS);
                tableView.scrollTo(newItem);
                tableView.getSelectionModel().select(newItem);
            });


            log.info("Добавлена группа пользователей {}", newItem.getName());
            AppStatic.createLog(true, String.format("Добавил группу пользователей '%s'", newItem.getName()));
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы пользователей {} произошла ошибка {}", newItem.getName(), e.getMessage());
        }

    }
}
