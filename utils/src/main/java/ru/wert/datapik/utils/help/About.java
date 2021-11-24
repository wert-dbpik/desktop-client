package ru.wert.datapik.utils.help;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.modal.ModalWindow;

import java.io.IOException;

public class About extends ModalWindow {

    public void create(){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/help/about.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/utils-css/pik-dark.css").toString());

            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
//
            Package aPackage = About.class.getPackage();
            String projectVersion = aPackage.getImplementationVersion();

            Label version = (Label)parent.lookup("#lblVersion");
//            version.setText("Версия " + projectVersion);
            version.setText("Версия " + AppStatic.CURRENT_ROJECT_VERSION);

            Label versionInfo = (Label)parent.lookup("#lblVersionInfo");
            versionInfo.setStyle("-fx-text-fill: #FFFF99");
            if(AppStatic.NEWER_PROJECT_VERSION != null)
                versionInfo.setText("доступна новая версия " + AppStatic.NEWER_PROJECT_VERSION);
            else
                versionInfo.setText("(это последняя версия)");

            AnchorPane anchorPane = (AnchorPane) parent.lookup("#modal_pane");
            anchorPane.setOnMouseClicked(AppStatic::closeWindow);

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
