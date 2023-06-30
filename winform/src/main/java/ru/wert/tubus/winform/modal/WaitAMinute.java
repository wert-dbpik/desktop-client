package ru.wert.tubus.winform.modal;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.io.IOException;

public class WaitAMinute extends ModalWindow{
    private static Stage stage;

    public static void create(){

        try {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(WaitAMinute.class.getResource("/winform-fxml/long_process/waitAMinute.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(WaitAMinute.class.getResource("/chogori-css/pik-dark.css").toString());
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);

            Platform.runLater(()->{
                centerWindow(stage, WinformStatic.WF_MAIN_STAGE, null);
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        stage.hide();
    }

}
