package ru.wert.datapik.utils.entities.drafts;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.winform.warnings.Warning2;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.statics.AppStatic.openDraftInPreviewer;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

public class Draft_OutstandingPreviewer {


    public void create(Stage owner, Draft draft) {

        StackPane rootPane = new StackPane();
        rootPane.setPrefWidth(800);
        rootPane.setPrefHeight(600);
        rootPane.getStylesheets().add(Warning2.class.getResource("/utils-css/pik-dark.css").toString());

        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        PreviewerPatchController previewerPatchController = previewerPatch.getController();
        previewerPatchController.initPreviewer(CH_PDF_VIEWER, CH_MAIN_STAGE.getScene());

        rootPane.getChildren().add(previewerPatch.getParent());
        openDraftInPreviewer(draft, previewerPatchController);

        new WindowDecoration("", rootPane, true, owner, true);

    }

}
