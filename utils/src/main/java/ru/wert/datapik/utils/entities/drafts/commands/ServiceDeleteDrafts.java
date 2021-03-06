package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.modal.LongProcess;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ITEM_IS_BUSY_MAYBE;

@Slf4j
public class ServiceDeleteDrafts extends Service<Void> {
    private final List<Draft> items;
    private final Draft_TableView tableView;
    private final double max;
    private double progress;
    private final int rowToBeSelected;

    public ServiceDeleteDrafts(List<Draft> items, Draft_TableView tableView) {
        this.items = items;
        this.tableView = tableView;
        this.rowToBeSelected = tableView.getItems().lastIndexOf(items.get(0));

        this.max = items.size();
        this.progress = 0.0;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    LongProcess.create("УДАЛЕНИЕ ДАННЫХ", this);
                });
                updateProgress(progress += 0.2, max); //Для затравочки

                for(Draft item : items){
                    //Удаляем запись чертежа из БД
                    try {
                        CH_QUICK_DRAFTS.delete(item);
                        log.info("Удалена запись о чертеже {}", item.toUsefulString());
                        AppStatic.createLog(false, String.format("Удалил чертеж '%s' (%s) из комплекта '%s'",
                                item.getPassport().toUsefulString(),
                                        EDraftType.getDraftTypeById(item.getDraftType()).getShortName() + "-" + item.getPageNumber(),
                                item.getFolder().toUsefulString()));
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
                    tableView.scrollTo(rowToBeSelected);
                    tableView.getSelectionModel().select(rowToBeSelected);
                });
                return null;
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

}

