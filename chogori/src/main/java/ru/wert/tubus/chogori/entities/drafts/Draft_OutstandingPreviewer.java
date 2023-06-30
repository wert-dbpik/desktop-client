package ru.wert.tubus.chogori.entities.drafts;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.previewer.PreviewerPatch;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.winform.warnings.Warning2;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.tubus.chogori.statics.AppStatic.openDraftInPreviewer;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Draft_OutstandingPreviewer {


    public void create(Stage owner, Draft draft) {

        StackPane rootPane = new StackPane();
        rootPane.setPrefWidth(800);
        rootPane.setPrefHeight(550);
        rootPane.getStylesheets().add(Warning2.class.getResource("/chogori-css/pik-dark.css").toString());

        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        PreviewerPatchController previewerPatchController = previewerPatch.getController();
        previewerPatchController.initPreviewer(CH_PDF_VIEWER, WF_MAIN_STAGE.getScene());
        previewerPatchController.initPreviewerToolBar(false, false, true, false, false);
        rootPane.getChildren().add(previewerPatch.getParent());
        openDraftInPreviewer(draft, previewerPatchController);

        new WindowDecoration("", rootPane, true, owner, true);

    }

}
