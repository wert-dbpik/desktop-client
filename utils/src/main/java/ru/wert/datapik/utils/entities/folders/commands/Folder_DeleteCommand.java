package ru.wert.datapik.utils.entities.folders.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Folder_DeleteCommand implements ICommand {

    private List<Folder> items;
    private Folder_TableView tableView;

    /**
     *
     * @param tableView Folder_TableView
     */
    public Folder_DeleteCommand(List<Folder> items, Folder_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Folder item : items){
            try {
                CH_QUICK_FOLDERS.delete(item);
                log.info("Удален пакет {}", item.toUsefulString());
            } catch (Exception e) {
                e.printStackTrace();
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении пакета {} произошла ошибка {}", item.toUsefulString(),  e.getMessage());
            }
        }

        tableView.updateCatalogView(tableView.getChosenCatalogItem(), true);

//            tableView.updateView();
        tableView.scrollTo(row);
        tableView.getSelectionModel().select(row);

    }
}
