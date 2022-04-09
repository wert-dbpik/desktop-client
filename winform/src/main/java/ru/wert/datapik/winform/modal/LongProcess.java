package ru.wert.datapik.winform.modal;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.datapik.winform.statics.WinformStatic.closeWindow;

public class LongProcess extends ModalWindow{
    private static Stage stage;
    public static ProgressBar progressBar;

    public static void create(String title, Task<Void> task){

        try {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(LongProcess.class.getResource("/winform-fxml/long_process/longProcess.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(LongProcess.class.getResource("/utils-css/pik-dark.css").toString());

            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Label lblTitle = (Label)parent.lookup("#lblTitle");
            lblTitle.setText(title);

            Label lblCountNewItems = (Label)parent.lookup("#lblCountNewItems");
            lblCountNewItems.textProperty().bind(task.messageProperty());

            Button btnStop = (Button)parent.lookup("#btnStop");
            btnStop.setOnAction((event -> {
                task.cancel();
                closeWindow(event);
            }));


            progressBar = (ProgressBar)parent.lookup("#progressBar");
            progressBar.progressProperty().bind(task.progressProperty());

            setMovingPane(parent);

            Platform.runLater(()->{
                centerWindow(stage, WF_MAIN_STAGE, null);
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
