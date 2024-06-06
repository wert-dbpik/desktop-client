package ru.wert.tubus.chogori.remarks;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.wert.tubus.chogori.components.ZoomableScrollPane;
import ru.wert.tubus.winform.warnings.Warning2;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.File;
import java.io.IOException;

public class Pic_OutstandingPreviewer {


    public void create(Stage owner, File file) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/remarks/PicturesViewer.fxml"));
            Parent parent = loader.load();
            StackPane container = (StackPane) parent.lookup("#spContainer");
            container.getStylesheets().add(Warning2.class.getResource("/chogori-css/pik-dark.css").toString());
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
