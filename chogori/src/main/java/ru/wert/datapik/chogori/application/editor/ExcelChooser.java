package ru.wert.datapik.chogori.application.editor;

import javafx.stage.FileChooser;

import java.io.File;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER_SETTINGS;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class ExcelChooser {

    public File choose(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("exel", "*.xlsx")
        );
        fileChooser.setInitialDirectory(new File(CH_CURRENT_USER_SETTINGS.getPathToNormyMK()));

        File chosenFile = new File("");
        try {
            chosenFile = fileChooser.showOpenDialog(WF_MAIN_STAGE);
        } catch (Exception e) {
            fileChooser.setInitialDirectory(new File("C:/"));
            chosenFile = fileChooser.showOpenDialog(WF_MAIN_STAGE);
            e.printStackTrace();
        }

        return chosenFile;
    }
}
