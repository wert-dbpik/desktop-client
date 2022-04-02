package ru.wert.datapik.chogori.application.app_window;

import com.sun.jmx.snmp.tasks.TaskServer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.serviceQUICK.*;
import ru.wert.datapik.client.interfaces.UpdatableTab;
import ru.wert.datapik.utils.tabs.AppTab;
import ru.wert.datapik.winform.modal.LongProcess;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;

@Slf4j
public class TaskUpdateData extends Task<Void> {

    private final double max;
    private double progress;
    private List<UpdatableTab> updatableTabList;

    public TaskUpdateData() {
        this.progress = 0.0;

        updatableTabList = new ArrayList<>();

        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof UpdatableTab)
                updatableTabList.add((UpdatableTab)((AppTab) tab).getTabController());
        }
        this.max = 7.0 + updatableTabList.size();
    }

    @Override
    protected Void call() throws Exception {

        Platform.runLater(() -> {
            LongProcess.create("ОБНОВЛЕНИЕ ДАННЫХ", this);
        });
        updateProgress(progress += 0.2, max); //Для затравочки

        PrefixQuickService.reload();
        updateProgress(progress = 1.0, max);

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

        for(UpdatableTab tab: updatableTabList){
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
    protected void updateProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(message);
    }
}
