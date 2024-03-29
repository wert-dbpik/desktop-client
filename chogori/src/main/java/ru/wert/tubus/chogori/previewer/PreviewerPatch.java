package ru.wert.tubus.chogori.previewer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import lombok.Getter;
import ru.wert.tubus.client.entity.models.Draft;

import java.io.IOException;

public class PreviewerPatch {

    @Getter private Parent parent;
    @Getter private PreviewerPatchController controller;
    @Getter private Label lblDraftInfo;


    public PreviewerPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/previewer/previewerPatch.fxml"));
            parent = loader.load();
            controller = loader.getController();
            lblDraftInfo = (Label) parent.lookup("#lblDraftInfo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Draft getCurrentDraft(){
        return controller.getCurrentDraft();
    }

}
