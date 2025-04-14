package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.modal.LongProcess;
import ru.wert.tubus.winform.warnings.Warning1;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class ServiceDeleteDrafts extends Service<Void> {
    private final List<Draft> items;
    private final Draft_TableView tableView;
    private final List<Draft> successfullyDeleted = new ArrayList<>();
    private final int rowToBeSelected;

    public ServiceDeleteDrafts(List<Draft> items, Draft_TableView tableView) {
        this.items = new ArrayList<>(items); // Создаем копию списка
        this.tableView = tableView;
        this.rowToBeSelected = tableView.getItems().lastIndexOf(items.get(0));
        log.debug("Необходимо удалить {} чертежей", items.size());
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> LongProcess.create("УДАЛЕНИЕ ДАННЫХ", this));

                double progress = 0;
                double max = items.size();
                updateProgress(progress, max);

                for (Draft item : items) {
                    boolean dbDeleted = false;
                    boolean fileDeleted = false;

                    // 1. Удаляем запись из БД
                    try {
                        log.debug("Удаление записи о чертеже {}", item.getPassport().toUsefulString());
                        CH_QUICK_DRAFTS.delete(item);
                        dbDeleted = true;
                        log.info("Удалена запись о чертеже {}", item.toUsefulString());
                        AppStatic.createLog(false, String.format("Удалил чертеж '%s' (%s) из комплекта '%s'",
                                item.getPassport().toUsefulString(),
                                EDraftType.getDraftTypeById(item.getDraftType()).getShortName() + "-" + item.getPageNumber(),
                                item.getFolder().toUsefulString()));
                    } catch (Exception e) {
                        log.error("Ошибка при удалении записи о чертеже: {}", e.getMessage());
                        Platform.runLater(() ->
                                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE));
                    }

                    // 2. Удаляем файл только если удаление из БД прошло успешно
                    if (dbDeleted) {
                        try {
                            log.debug("Удаление файла чертежа {}", item.getPassport().toUsefulString());
                            String fileName = item.getId() + "." + item.getExtension();
                            CH_QUICK_DRAFTS.deleteDraftFile("drafts", fileName);
                            fileDeleted = true;
                            log.info("Удален файл чертежа {}", item.toUsefulString());
                        } catch (Exception e) {
                            log.error("Ошибка при удалении файла чертежа: {}", e.getMessage());
                            Platform.runLater(() ->
                                    Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE));
                        }
                    }

                    // 3. Если оба удаления успешны - добавляем в список для удаления из таблицы
                    if (dbDeleted && fileDeleted) {
                        successfullyDeleted.add(item);
                    }

                    updateProgress(++progress, max);
                }

                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        super.succeeded();

        if (!successfullyDeleted.isEmpty()) {
            // Удаляем из таблицы
            Platform.runLater(() -> {
                tableView.getItems().removeAll(successfullyDeleted);
                tableView.updateView();

                // Выбираем следующий элемент после удаленных
                if (!tableView.getItems().isEmpty()) {
                    int pos = Math.max(0, Math.min(rowToBeSelected, tableView.getItems().size() - 1));
                    tableView.getSelectionModel().select(pos);
                    tableView.scrollTo(pos);
                }
            });
        }

        LongProcess.close();
    }

    @Override
    protected void failed() {
        super.failed();
        log.error("Ошибка при удалении чертежей", getException());
        LongProcess.close();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        LongProcess.close();
    }
}
