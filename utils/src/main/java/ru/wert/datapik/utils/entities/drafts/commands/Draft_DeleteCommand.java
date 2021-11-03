package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.winform.modal.LongProcess;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Draft_DeleteCommand implements ICommand {

    private List<Draft> items;
    private Draft_TableView tableView;

    /**
     *
     * @param tableView Draft_TableView
     */
    public Draft_DeleteCommand(List<Draft> items, Draft_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        Task<Void> deleteDrafts = new Task<Void>(){

//            ProgressIndicator progressIndicator = new ProgressIndicator();
            double max = items.size();
            double progress = 0.0;

            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    LongProcess.create("УДАЛЕНИЕ ДАННЫХ", this);
                });
                updateProgress(progress += 0.2, max); //Для затравочки
                int row = tableView.getItems().lastIndexOf(items.get(0));

                for(Draft item : items){
                    //Удаляем запись чертежа из БД
                    try {
                        CH_QUICK_DRAFTS.delete(item);
                        log.info("Удалена запись о чертеже {}", item.toUsefulString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                        log.error("При удалении записи о чертеже {} произошла ошибка", item.toUsefulString());
                    }

                    //Удаляем соответствующий файл чертежа из БД
                    try {
                        String fileName = item.getId() + "." + item.getExtension();
                        CH_QUICK_DRAFTS.deleteDraftFile("drafts", fileName);
                        log.info("Удален файл чертежа {}", item.toUsefulString());
                    } catch (Exception e) {
                        Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                        log.error("При удалении файла чертежа {} произошла ошибка", item.toUsefulString());
                        e.printStackTrace();
                    }
                    updateProgress(progress += 1.0, max);
                }
                Platform.runLater(()->{
                    tableView.updateView();
                    tableView.scrollTo(row);
                    tableView.getSelectionModel().select(row);
                });
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                LongProcess.close();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                LongProcess.close();
            }

            @Override
            protected void updateProgress(long workDone, long max) {
                super.updateProgress(workDone, max);
            }

            @Override
            protected void updateMessage(String message) {
                super.updateMessage(message);
            }

        };
        new Thread(deleteDrafts).start();
    }
}
