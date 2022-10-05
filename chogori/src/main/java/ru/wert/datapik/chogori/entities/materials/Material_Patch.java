package ru.wert.datapik.chogori.entities.materials;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

import java.io.IOException;

public class Material_Patch {

    @Getter private Parent parent;
    @Getter private Material_PatchController materialPatchController;

    public Material_Patch create(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/materials/materialsPatch.fxml"));
            parent = loader.load();
            materialPatchController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

}
