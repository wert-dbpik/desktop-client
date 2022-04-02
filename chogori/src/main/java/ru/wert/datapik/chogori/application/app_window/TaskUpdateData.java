package ru.wert.datapik.chogori.application.app_window;

import com.sun.jmx.snmp.tasks.TaskServer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.serviceQUICK.*;
import ru.wert.datapik.winform.modal.LongProcess;

@Slf4j
public class TaskUpdateData extends Task<Void> {

    private final double max;
    private double progress;

    public TaskUpdateData() {

        this.max = 8.0;
        this.progress = 0.0;

    }

    @Override
    protected Void call() throws Exception {

        Platform.runLater(() -> {
            LongProcess.create("ОБНОВЛЕНИЕ ДАННЫХ", this);
        });
        updateProgress(progress += 0.2, max); //Для затравочки

        PrefixQuickService.reload();
        updateProgress(1.0, max);

        AnyPartQuickService.reload();
        updateProgress(2.0, max);

        PassportQuickService.reload();
        updateProgress(3.0, max);

        FolderQuickService.reload();
        updateProgress(4.0, max);

        DraftQuickService.reload();
        updateProgress(5.0, max);

        MaterialQuickService.reload();
        updateProgress(6.0, max);

        DetailQuickService.reload();
        updateProgress(7.0, max);

//        for(Tab tab: CH_TAB_PANE.getTabs()){
//            if(tab.get instanceof UpdatableTab) taupdateTab();
//        }

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
