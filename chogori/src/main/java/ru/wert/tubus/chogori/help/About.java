package ru.wert.tubus.chogori.help;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.tubus.chogori.images.AppImages;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.winform.modal.ModalWindow;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.io.IOException;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class About extends ModalWindow {

    public void create(){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/help/about.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/pik-dark.css").toString());

            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
//
            Package aPackage = About.class.getPackage();
            String projectVersion = aPackage.getImplementationVersion();

            ImageView logo = (ImageView)parent.lookup("#logo");
            logo.setImage(AppImages.LOGO_IMG);

            Label appName = (Label)parent.lookup("#appName");
            appName.setText(WinformStatic.PROGRAM_NAME);

            Label version = (Label)parent.lookup("#lblVersion");
//            version.setText("Версия " + projectVersion);
            version.setText("Версия " + WinformStatic.CURRENT_PROJECT_VERSION);

            //ВЕРСИЯ
            Label versionInfo = (Label)parent.lookup("#lblVersionInfo");
            versionInfo.setStyle("-fx-text-fill: #FFFF99");
            String currentVersion = WinformStatic.CURRENT_PROJECT_VERSION;
            String inDataBaseVersion = WinformStatic.LAST_VERSION_IN_DB;
            if(currentVersion.compareTo(inDataBaseVersion) == 0)
                versionInfo.setText("(это последняя версия)");
            else if(currentVersion.compareTo(inDataBaseVersion) < 0)
                versionInfo.setText("доступна новая версия " + WinformStatic.LAST_VERSION_IN_DB);
            else
                versionInfo.setText("Это beta версия");


            AnchorPane anchorPane = (AnchorPane) parent.lookup("#about_pane");
            anchorPane.setOnMouseClicked(AppStatic::closeWindow);

            Platform.runLater(()->{
                ModalWindow.centerWindow(stage, WF_MAIN_STAGE, null);
            });
            stage.isAlwaysOnTop();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
