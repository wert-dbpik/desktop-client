package ru.wert.datapik.utils.entities.drafts;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.Comparators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * В методе отображается самый первый чертеж из списка
     */
    public void connectWithPreviewer(Draft_TableView draftsTable, PreviewerPatchController previewerPatchController){
        draftsTable.getPreparedList().addListener((observable, oldValue, newValue) -> {
            Draft currentlyShownDraft = previewerPatchController.getCurrentDraft();
            List<Draft> drafts = new ArrayList<>(newValue);
            if (!drafts.isEmpty()) {
                drafts.sort(Comparators.draftsForPreviewerComparator());
                AppStatic.openDraftInPreviewer(drafts.get(0), previewerPatchController);
            } else {
                //Отображаем NO_IMAGE
                AppStatic.openDraftInPreviewer(null, previewerPatchController);
            }
        });
    }



}
