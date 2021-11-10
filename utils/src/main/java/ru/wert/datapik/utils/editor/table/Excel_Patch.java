package ru.wert.datapik.utils.editor.table;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

import java.io.IOException;

public class Excel_Patch {

    @Getter private Parent parent;
    @Getter private Excel_PatchController excelPatchController;

    public Excel_Patch create(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/editor/excelPatch.fxml"));
            parent = loader.load();
            excelPatchController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

}
