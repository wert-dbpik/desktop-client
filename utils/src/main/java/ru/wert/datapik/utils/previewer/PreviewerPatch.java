package ru.wert.datapik.utils.previewer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class PreviewerPatch {

    private Parent previewer;
    private PreviewerPatchController controller;

    public PreviewerPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/previewer/previewerPatch.fxml"));
            previewer = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public PreviewerPatchController getController() {
        return controller;
    }

    public Parent getParent() {
        return previewer;
    }
}
