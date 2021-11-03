package ru.wert.datapik.utils.entities.passports;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;

import java.io.IOException;

public class Passport_Patch {

    @Getter private Parent parent;
    @Getter private Passport_PatchController passportPatchController;

    public Passport_Patch create(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/passports/passportsPatch.fxml"));
            parent = loader.load();
            passportPatchController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

}
