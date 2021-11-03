package ru.wert.datapik.utils.entities.drafts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

import java.io.IOException;

public class Draft_Patch {

    @Getter private Parent parent;
    @Getter private Draft_PatchController draftPatchController;

    public Draft_Patch create(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/drafts/draftsPatch.fxml"));
            parent = loader.load();
            draftPatchController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

}
