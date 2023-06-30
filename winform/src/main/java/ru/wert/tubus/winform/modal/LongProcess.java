package ru.wert.tubus.winform.modal;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.io.IOException;

public class LongProcess extends ModalWindow{
    private static Stage stage;
    public static ProgressBar progressBar;

    public static void create(String title, Task<Void> task){

        try {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(LongProcess.class.getResource("/chogori-fxml/longProcess/longProcess.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(LongProcess.class.getResource("/chogori-css/pik-dark.css").toString());
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.TRANSPARENT);

            Label lblTitle = (Label)parent.lookup("#lblTitle");
            lblTitle.setText(title);

            Label lblCountNewItems = (Label)parent.lookup("#lblCountNewItems");
            lblCountNewItems.textProperty().bind(task.messageProperty());

            Button btnStop = (Button)parent.lookup("#btnStop");
            btnStop.setOnAction((event -> {
                task.cancel();
                WinformStatic.closeWindow(event);
            }));

            parent.setOnKeyTyped(e->{
                if(e.getCode().equals(KeyCode.ESCAPE)) {
                    task.cancel();
                    WinformStatic.closeWindow(e);
                }
            });


            progressBar = (ProgressBar)parent.lookup("#progressBar");
            progressBar.progressProperty().bind(task.progressProperty());

            setMovingPane(parent);

            Platform.runLater(()->{
                centerWindow(stage, WinformStatic.WF_MAIN_STAGE, null);
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        stage.hide();
    }

}
