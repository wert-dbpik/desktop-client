package ru.wert.datapik.chogori.application.editor;

import javafx.stage.FileChooser;

import java.io.File;

import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

public class ExcelChooser {

    public File choose(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("exel", "*.xlsx")
        );
        fileChooser.setInitialDirectory(new File("C:/test"));

        return fileChooser.showOpenDialog(CH_MAIN_STAGE);
    }
}
