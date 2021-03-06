package ru.wert.datapik.utils.excel.table;

import javafx.stage.FileChooser;
import ru.wert.datapik.utils.excel.poi.POIReader;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class ExcelOpener {

    POIReader poiReader;
    public void open(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("exel", "*.xlsx")
        );
        fileChooser.setInitialDirectory(new File("C:/test"));
        File selectedFile = fileChooser.showOpenDialog(WF_MAIN_STAGE);
        if (selectedFile == null) return;
        try {
            poiReader = new POIReader(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        showEditor(selectedFile.getName());
    }
}
