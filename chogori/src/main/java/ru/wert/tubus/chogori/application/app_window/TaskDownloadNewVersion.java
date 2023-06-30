package ru.wert.tubus.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.winform.modal.LongProcess;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

@Slf4j
public class TaskDownloadNewVersion extends Task<Void> {

    private File sourceFile, destFile;

    public TaskDownloadNewVersion() {

        sourceFile = new File(AppStatic.findCurrentLastAppVersion().getPath());
        if (!sourceFile.exists()) {
            Warning1.create("ОШИБКА!", "Не удалось скачать новую версию!", "Файл в репозитории отсутствует");
            log.error("File of new version doesn't exist in repository '{}'", sourceFile);
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(sourceFile.getName());
        File initDir = new File(System.getProperty("user.home") + "/Desktop");
        if (!initDir.exists()) initDir = new File("C:\\");
        chooser.setInitialDirectory(initDir);
        chooser.setTitle("Выберите директорию для сохранения");
        destFile = chooser.showSaveDialog(WF_MAIN_STAGE);
        if (destFile == null) return;
        log.debug("downloadLastVersion : sourceFile = " + sourceFile);
        log.debug("downloadLastVersion : sourceFile = " + destFile);


        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    protected Void call() throws Exception {

        Platform.runLater(()->{
            LongProcess.create("СКАЧИВАНИЕ НОВОЙ ВЕРСИИ", this);
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
