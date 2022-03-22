package ru.wert.datapik.winform.warnings;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.winform.enums.ESolution;
import ru.wert.datapik.winform.modal.ModalWindow;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.io.IOException;

public class WarningDuplicateDraftFound extends ModalWindow {

    public static ObjectProperty<ESolution> solution = new SimpleObjectProperty<>();


    public ESolution create(Long draftId, String draftName){

       try {
            Stage stage = new Stage();
            FXMLLoader userDialogLoader = new FXMLLoader(WarningDuplicateDraftFound.class.getResource("/winform-fxml/warnings/warningDuplicateDraftFound.fxml"));
            Parent parent = userDialogLoader.load();
            parent.getStylesheets().add(WarningDuplicateDraftFound.class.getResource("/utils-css/pik-dark.css").toString());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Button btnDelete = (Button)parent.lookup("#btnDelete");
            btnDelete.setOnAction((event -> {
                solution.set(ESolution.DELETE);
                WinformStatic.closeWindow(event);

            }));

           Button btnChange = (Button) parent.lookup("#btnChange");
           btnChange.setOnAction((event -> {
               solution.set(ESolution.CHANGE);
               WinformStatic.closeWindow(event);

            }));

            Button btnCancel = (Button)parent.lookup("#btnCancel");
            btnCancel.setOnAction((event -> {
                solution.set(ESolution.CANCEL);
                WinformStatic.closeWindow(event);

            }));

            Label lblTitle = (Label)parent.lookup("#lblWarningTitle");
            lblTitle.setText("ВИМАНИЕ!");
            lblTitle.setStyle("-fx-text-fill: #fcce45");

            Label lblProblem = (Label)parent.lookup("#lblProblem");
            lblProblem.setText("Обнаружен ДЕЙСТВУЮЩИЙ чертеж\n" + draftName);
            lblProblem.setStyle("-fx-text-fill: #fcce45");

            Label lblDecision = (Label)parent.lookup("#lblDecision");
            lblDecision.setText("Что с ним делать?");



            ModalWindow.setMovingPane(parent);

            Platform.runLater(()->{
                ModalWindow.centerWindow(stage);
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return solution.get();

    }

}

