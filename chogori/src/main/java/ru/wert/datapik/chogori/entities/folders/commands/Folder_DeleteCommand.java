package ru.wert.datapik.chogori.entities.folders.commands;

import javafx.application.Platform;
import javafx.concurrent.Service;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.chogori.entities.drafts.commands.ServiceDeleteDrafts;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.chogori.services.ChogoriServices.CH_QUICK_FOLDERS;
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
        //Собираем по папкам чертежи
        List<Draft> draftsToDelete = new ArrayList<>();
        for(Folder item : items){
            draftsToDelete.addAll(CH_QUICK_DRAFTS.findAllByFolder(item));
        }

        //Для пустой папки все просто
        if(draftsToDelete.isEmpty()){
            deleteFolder(row);
            return;
        }

        boolean decision = Warning2.create("ВНИМАНИЕ!",
                "При удалении папки удаляется ее содержимое",
                String.format("Будут удалены %s чертежей!", draftsToDelete.size()));
        if(!decision) return;


        Service<Void> deleteDrafts = new ServiceDeleteDrafts(draftsToDelete, tableView.getDraftTable());

        deleteDrafts.setOnSucceeded(e->{
            deleteFolder(row);
        });

        deleteDrafts.restart();

    }

    private void deleteFolder(int row) {
        for (Folder item : items) {
            try {
                CH_QUICK_FOLDERS.delete(item);
                log.info("Удален пакет {}", item.toUsefulString());
                AppStatic.createLog(false, String.format("Удалил комплект '%s'", item.toUsefulString()));
            } catch (Exception ex) {
                ex.printStackTrace();
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении пакета {} произошла ошибка {}", item.toUsefulString(), ex.getMessage());
            }
        }

        Platform.runLater(() -> {
            tableView.updateVisibleLeafOfTableView(tableView.getUpwardRow().getValue());
            tableView.scrollTo(row);
            tableView.getSelectionModel().select(row);
        });
    }
}
