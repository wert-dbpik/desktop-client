package ru.wert.tubus.chogori.entities.user_groups.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.user_groups.UserGroup_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class UserGroupDeleteCommand implements ICommand {

    private List<UserGroup> items;
    private UserGroup_TableView tableView;

    /**
     *
     * @param tableView UserGroup_TableView
     */
    public UserGroupDeleteCommand(List<UserGroup> items, UserGroup_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(UserGroup item : items){
            try {
                ChogoriServices.CH_USER_GROUPS.delete(item);
                log.info("Удалена группа пользователей {}", item.getName());
                AppStatic.createLog(true, String.format("Удалил группу пользователей '%s'", item.getName()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении группы пользователей {} произошла ошибка {}", item.getName(), e.getMessage());

            }
        }

        tableView.easyUpdate(ChogoriServices.CH_USER_GROUPS);

        tableView.getSelectionModel().select(row);

    }
}
