package ru.wert.datapik.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.chogori.application.editor.ExcelEditorNewController;
import ru.wert.datapik.winform.modal.LongProcess;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;

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
            CH_TAB_PANE.createNewTab(chosenFile.getName(), parent, true, null, null);
        });


        return null;
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
        LongProcess.close();
    }
}
