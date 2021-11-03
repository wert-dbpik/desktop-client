package ru.wert.datapik.winform.warnings;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.winform.modal.ModalWindow;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.io.IOException;

public class Warning2 extends ModalWindow {

    private static BooleanProperty agreevation = new SimpleBooleanProperty();

    public static boolean create(String title, String problem, String decision){

        try {
            Stage stage = new Stage();
            FXMLLoader userDialogLoader = new FXMLLoader(Warning2.class.getResource("/winform-fxml/warnings/warning2.fxml"));
            Parent parent = userDialogLoader.load();
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Button btnOK = (Button)parent.lookup("#btnYes");
            btnOK.setOnAction((event -> {
                agreevation.set(true);
                WinformStatic.closeWindow(event);

            }));

            Button btnCancel = (Button)parent.lookup("#btnCancel");
            btnCancel.setOnAction((event -> {
                agreevation.set(false);
                WinformStatic.closeWindow(event);

            }));

            Label lblTitle = (Label)parent.lookup("#lblTitle");
            lblTitle.setText(title);

            Label lblProblem = (Label)parent.lookup("#lblProblem");
            lblProblem.setText(problem);

            Label lblDecision = (Label)parent.lookup("#lblDecision");
            lblDecision.setText(decision);



            ModalWindow.setMovingPane(parent);

            Platform.runLater(()->{
                ModalWindow.centerWindow(stage);
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return agreevation.get();
    }

}
