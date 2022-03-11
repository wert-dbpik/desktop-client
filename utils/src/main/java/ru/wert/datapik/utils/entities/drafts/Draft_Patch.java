package ru.wert.datapik.utils.entities.drafts;

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

    public void connectWithPreviewer(Draft_TableView draftsTable, PreviewerPatchController previewerPatchController){

        //Для отображения чертежа по умолчанию
        draftsTable.getPreparedList().addListener((observable, oldValue, newValue) -> {
            Draft currentlyShownDraft = previewerPatchController.getCurrentDraft();
            List<Draft> drafts = new ArrayList<>(newValue);
            if (!drafts.isEmpty()) {
                drafts.sort(Comparators.draftsForPreviewerComparator());
                Draft defaultShownDraft = drafts.get(0);
//                    if(currentlyShownDraft != null && !defaultShownDraft.getId().equals(currentlyShownDraft.getId()))
                AppStatic.openDraftInPreviewer(drafts.get(0), previewerPatchController);
            } else {
                //Отображаем NO_IMAGE
                if(currentlyShownDraft != null)
                    AppStatic.openDraftInPreviewer(null, previewerPatchController);
            }

        });
    }

}
