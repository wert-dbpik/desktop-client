package ru.wert.datapik.utils.remarks;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.components.ZoomableScrollPane;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.winform.warnings.Warning2;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.statics.AppStatic.openDraftInPreviewer;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Pic_OutstandingPreviewer {


    public void create(Stage owner, File file) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/remarks/PicturesViewer.fxml"));
            Parent parent = loader.load();
            StackPane container = (StackPane) parent.lookup("#spContainer");
            container.getStylesheets().add(Warning2.class.getResource("/utils-css/pik-dark.css").toString());
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);

            ScrollPane pane = new ZoomableScrollPane(imageView, container);
            pane.setPrefWidth(800);
            pane.setPrefHeight(600);
            container.getChildren().add(pane);
            new WindowDecoration("", parent, true, owner, false);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
