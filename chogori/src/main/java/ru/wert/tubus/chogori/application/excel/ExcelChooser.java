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
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Открыть файл");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файлы EXCEL (.xlsx)", "*.xlsx"));
        System.out.println(ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToNormyMK());
        File normyMKDirectory = new File(ChogoriSettings.CH_CURRENT_USER_SETTINGS == null ? "C:/" : ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToNormyMK());
        chooser.setInitialDirectory(normyMKDirectory.exists() ? normyMKDirectory : new File("C:/"));

        File chosenFile = new File("");
        try {
            chosenFile = chooser.showOpenDialog(WF_MAIN_STAGE);
            if(chosenFile == null) return null;
        } catch (Exception e) {
            chooser.setInitialDirectory(new File("C:/"));
            chosenFile = chooser.showOpenDialog(WF_MAIN_STAGE);
            e.printStackTrace();
        }

        return createTempCopyOfFile(chosenFile);
    }

    public File createTempCopyOfFile(File oldFile){
        File copied = null;
        try {
            copied = new File(WF_TEMPDIR, oldFile.getName() + new Date().getTime());
            Files.copy(oldFile.toPath(), copied.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copied;
    }
}
