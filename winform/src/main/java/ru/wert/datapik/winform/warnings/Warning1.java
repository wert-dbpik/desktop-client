package ru.wert.datapik.winform.warnings;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.winform.modal.ModalWindow;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Warning1 extends ModalWindow {

    public static void create(String title, String problem, String decision){


        try {
            Stage stage = new Stage();
            FXMLLoader userDialogLoader = new FXMLLoader(Warning1.class.getResource("/winform-fxml/warnings/warning1.fxml"));
            Parent parent = userDialogLoader.load();
            parent.getStylesheets().add(Warning2.class.getResource("/chogori-css/pik-dark.css").toString());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Button btnOK = (Button)parent.lookup("#btnOK");
            btnOK.setOnAction((event -> {
                ((Node)event.getSource()).getScene().getWindow().hide();

            }));

            Label lblTitle = (Label)parent.lookup("#lblTitle");
            lblTitle.setText(title);

            Label lblProblem = (Label)parent.lookup("#lblProblem");
            lblProblem.setText(problem);

            Label lblDecision = (Label)parent.lookup("#lblDecision");
            lblDecision.setText(decision);

            ModalWindow.setMovingPane(parent);

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
