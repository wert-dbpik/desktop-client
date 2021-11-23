package ru.wert.datapik.utils.help;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.modal.ModalWindow;

import java.io.IOException;

import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;

public class About extends ModalWindow {

    public void create(){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/help/about.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Text text = (Text)parent.lookup("#tVersion");
            text.setText("Version 1.1.0");

            AnchorPane anchorPane = (AnchorPane) parent.lookup("#modal_pane");
            anchorPane.setOnMouseClicked(AppStatic::closeWindow);
            anchorPane.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());

            Platform.runLater(()->{
                ModalWindow.centerWindow(stage);
            });
            stage.isAlwaysOnTop();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
