package ru.wert.tubus.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.winform.modal.LongProcess;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class TaskDownloadDXFDocks extends Task<Void> {

    private File sourceFile, destDir;
    private List<Draft> filesToBeDownloaded;

    public TaskDownloadDXFDocks(List<Draft> filesToBeDownloaded) {
        this.filesToBeDownloaded = filesToBeDownloaded;

        DirectoryChooser chooser = new DirectoryChooser();
        File initDir = new File(System.getProperty("user.home") + "/Desktop");
        if (!initDir.exists()) initDir = new File("C:\\");
        chooser.setInitialDirectory(initDir);
        chooser.setTitle("Выберите директорию для сохранения");
        destDir = chooser.showDialog(WF_MAIN_STAGE);
        if (destDir == null) return;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    protected Void call() throws Exception {

        Platform.runLater(()->{
            LongProcess.create("СКАЧИВАНИЕ DXF", this);
        });
        for(Draft d: filesToBeDownloaded){
            downloadFile(d);
        }
        return null;

    }

    private boolean downloadFile(Draft draft){
        //Имя файла - id файла
        Long fileId = draft.getId();
        //и расширение
        String ext = draft.getExtension();

        //Формируем имя скачиваемого файла типа '745222.020-03 Деталька.dxf'
        StringBuilder newName = new StringBuilder("");
        newName.append(AppStatic.getShortDecNumber(draft.getDecimalNumber()));
        if(draft.getPageNumber() != 0)
            newName.append("-").append(String.format("%02d", draft.getPageNumber()));
        newName.append(" ").append(draft.getName()).append(".").append(ext);

        boolean res = ChogoriServices.CH_FILES.download("drafts", //Постоянная папка в каталоге для чертежей
                String.valueOf(fileId), //название скачиваемого файла
                "." + ext, //расширение скачиваемого файла
                destDir.toString(),  //временная папка, куда необходимо скачать файл
                null,
                newName.toString()); //префикс
        if(res) {
            AppStatic.createLog(true, String.format("Скачал файл DXF '%s' в папку '%s'", draft.toUsefulString(), destDir.toString()));
            log.info("TaskDownloadDXFDocks : файл DXF '{}' загружен c сервера в папку '{}'", draft.toUsefulString(), destDir.toString());
        } else {
            log.error("TaskDownloadDXFDocks : при загрузе файла DXF '{}' c сервера в папку '{}' произошла ошибка", draft.toUsefulString(), destDir.toString());
            Platform.runLater(()->Warning1.create($ATTENTION, $DRAFT_IS_NOT_AVAILABLE, $MAYBE_IT_IS_CORRUPTED));
            return false;
        }
        return true;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
//        log.info("File of new version '{}' was downloaded successfully!", sourceFile.getName());
        Platform.runLater(()->{
            Warning1.create("ОТЛИЧНО!",
                   "Загрузка успешно завершена!",
                    "Можете раслабиться, выпейте чашечку кофе!");
        });
        LongProcess.close();
    }

    @Override
    protected void failed() {
        super.failed();
//        log.info("Downloading of new version '{}' failed!", sourceFile.getName());
        LongProcess.close();
        Platform.runLater(()->{
            Warning1.create("ОШИБКА!",
                    "Развертки не удалось загрузить",
                    "Попробуйте загрузить файлы позже или получить их другим способом");
        });
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        LongProcess.close();
    }
}
