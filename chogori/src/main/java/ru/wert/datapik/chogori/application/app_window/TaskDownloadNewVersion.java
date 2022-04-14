package ru.wert.datapik.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.modal.LongProcess;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

@Slf4j
public class TaskDownloadNewVersion extends Task<Void> {

    private File sourceFile, destFile;

    public TaskDownloadNewVersion( File sourceFile, File destFile) {
        this.sourceFile = sourceFile;
        this.destFile = destFile;
    }

    @Override
    protected Void call() throws Exception {

        Platform.runLater(()->{
            LongProcess.create("ЗАГРУЗКА НОВОЙ ВЕРСИИ", this);
        });

        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return null;

    }

    @Override
    protected void succeeded() {
        super.succeeded();
        log.info("File of new version '{}' was downloaded successfully!", sourceFile.getName());
        LongProcess.close();
        Platform.runLater(()->{
        Warning1.create("ПОЗДРАВЛЯЮ!",
                String.format("Файл %s успешно загружен", sourceFile.getName()),
                "Закройте это окно и запустите новую версию программы\nФайл предыдущей версии можете удалить");
        });

    }

    @Override
    protected void failed() {
        super.failed();
        log.info("Downloading of new version '{}' failed!", sourceFile.getName());
        LongProcess.close();
        Platform.runLater(()->{
            Warning1.create("ОШИБКА!",
                    String.format("Не удалось загрузить файл %s", sourceFile.getName()),
                    String.format("Возможно, произошла ошибка доступа\nПопробуйте скачать файл вручную:\n%s", sourceFile));
        });
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        LongProcess.close();
    }
}
