package ru.wert.datapik.chogori.entities.users.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.users.User_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.chogori.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class UserDeleteCommand implements ICommand {

    private List<User> items;
    private User_TableView tableView;

    /**
     *
     * @param tableView User_TableView
     */
    public UserDeleteCommand(List<User> items, User_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(User item : items){
            try {
                CH_USERS.delete(item);
                log.info("Удален пользователь {}", item.getName());
                AppStatic.createLog(true, String.format("Удалил пользователя '%s'", item.getName()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении пользователя {} произошла ошибка {}", item.getName(), e.getMessage());
            }
        }

        tableView.easyUpdate(CH_USERS);

        tableView.getSelectionModel().select(row);

    }
}
