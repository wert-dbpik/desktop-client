package ru.wert.tubus.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.services.BatchResponse;
import ru.wert.tubus.chogori.application.services.BatchService;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.application.services.LocalCacheManager;
import ru.wert.tubus.client.entity.serviceQUICK.*;
import ru.wert.tubus.client.interfaces.UpdatableTabController;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.winform.modal.LongProcess;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

@Slf4j
public class TaskUpdateData extends Task<Void> {

    private final double max;
    private double progress;
    private final List<UpdatableTabController> updatableTabControllerList;

    public TaskUpdateData() {
        this.progress = 0.0;

        updatableTabControllerList = new ArrayList<>();

        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof UpdatableTabController)
                updatableTabControllerList.add((UpdatableTabController)((AppTab) tab).getTabController());
        }
        this.max = 8.0 + updatableTabControllerList.size(); // Увеличили max на 1 для нового шага
    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(() -> {
            LongProcess.create("ОБНОВЛЕНИЕ ДАННЫХ И КЭША", this);
        });
        updateProgress(progress += 0.2, max);

        // 1. Очищаем кэш
        updateMessage("\nОчистка кэша...");
        try {
            Files.walk(LocalCacheManager.CACHE_DIR)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            log.debug("Удален файл кэша: {}", file);
                        } catch (IOException e) {
                            log.error("Ошибка удаления файла кэша: {}", file, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Ошибка очистки кэша", e);
        }
        updateProgress(progress += 1.0, max);

        // 2. Загружаем свежие данные с сервера
        updateMessage("\nЗагрузка данных с сервера...");
        BatchResponse freshData = BatchService.loadInitialData();
        updateProgress(progress += 1.0, max);

        // 3. Сохраняем данные в кэш
        updateMessage("\nСохранение данных в кэш...");
        LocalCacheManager.saveToCache("initial_data", freshData);
        updateProgress(progress += 1.0, max);

        // Остальной код остается без изменений
        // 4. Обновляем QuickServices
        updateMessage("\nОбновление сервисов...");
        Platform.runLater(() -> ChogoriServices.initFromBatch(freshData));
        updateProgress(progress += 1.0, max);

        // 5. Обновляем отдельные сервисы (если нужно)
        PrefixQuickService.reload();
        updateProgress(progress += 1.0, max);

        AnyPartQuickService.reload();
        updateProgress(progress += 1.0, max);

        PassportQuickService.reload();
        updateProgress(progress += 1.0, max);

        FolderQuickService.reload();
        updateProgress(progress += 1.0, max);

        DraftQuickService.reload();
        updateProgress(progress += 1.0, max);

        MaterialQuickService.reload();
        updateProgress(progress += 1.0, max);

        DetailQuickService.reload();
        updateProgress(progress += 1.0, max);

        // 6. Обновляем вкладки
        updateMessage("\nОбновление интерфейса...");
        for(UpdatableTabController tab: updatableTabControllerList){
            Platform.runLater(tab::updateTab);
            updateProgress(progress += 1.0, max);
        }

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
    protected void failed() {
        super.failed();
        LongProcess.close();
        Platform.runLater(() -> {
            Warning1.create("ОШИБКА", "Не удалось обновить данные", getException().getMessage());
        });
    }
}
