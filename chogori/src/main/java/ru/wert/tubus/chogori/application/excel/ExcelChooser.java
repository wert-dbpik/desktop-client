package ru.wert.tubus.chogori.application.excel;

import javafx.stage.FileChooser;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_TEMPDIR;

public class ExcelChooser {

    public File choose(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("exel", "*.xlsx")
        );
        File normyMKDirectory = new File(ChogoriSettings.CH_CURRENT_USER_SETTINGS == null ? "C:/" : ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToNormyMK());
        fileChooser.setInitialDirectory(normyMKDirectory.exists() ? normyMKDirectory : new File("C:/"));

        File chosenFile = new File("");
        try {
            chosenFile = fileChooser.showOpenDialog(WF_MAIN_STAGE);
        } catch (Exception e) {
            fileChooser.setInitialDirectory(new File("C:/"));
            chosenFile = fileChooser.showOpenDialog(WF_MAIN_STAGE);
            e.printStackTrace();
        }

        return createTempCopyOfFile(chosenFile);
    }

    public File createTempCopyOfFile(File oldFile){
        File copied = null;
        try {
//          copied = File.createTempFile(oldFile.getName(), "excel.tmp", tempDir);  //не работает
            copied = new File(WF_TEMPDIR, oldFile.getName() + new Date().getTime());
            Files.copy(oldFile.toPath(), copied.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copied;
    }
}
