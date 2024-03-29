package ru.wert.tubus.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.excel.ExcelEditorNewController;
import ru.wert.tubus.winform.modal.LongProcess;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.File;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;
@Slf4j
public class TaskOpenExcelFile extends Task<Void> {

    private File chosenFile;

    public TaskOpenExcelFile(File chosenFile) {
        this.chosenFile = chosenFile;


    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(()->{
            LongProcess.create("ОТКРЫТИЕ ФАЙЛА " + chosenFile.getName(), this);
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/excel/excelEditorNew.fxml"));
        Parent parent = loader.load();
        ExcelEditorNewController controller = loader.getController();
        controller.init(chosenFile);

        parent.getStylesheets().add(this.getClass().getResource("/chogori-css/drafts-dark.css").toString());
        Platform.runLater(()->{
            String tabName = chosenFile.getName();
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, null);
        });


        return null;
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        log.debug("Downloading of Excel-file {} has been cancelled", chosenFile.getName());
        LongProcess.close();
    }

    @Override
    protected void failed() {
        super.failed();
        LongProcess.close();
        log.error("Excel-file Loading failed with an error \n{}", getException().getMessage());
        Platform.runLater(()->{
            Warning1.create("ОШИБКА!",
                    "Не удалось загрузить файл",
                    "Возможно, файл не соответствует стандарту оформления\n" +
                            "или произошел конфликт с версией установленного Excel");
        });

    }

    @Override
    protected void succeeded() {
        super.succeeded();
        log.debug("Excel-file {} has been downloaded successfully", chosenFile.getName());
        LongProcess.close();
    }
}
