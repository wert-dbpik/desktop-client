package ru.wert.datapik.utils.entities.drafts;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.winform.enums.ESolution;
import ru.wert.datapik.winform.modal.ModalWindow;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Draft_DuplicateDraftFound extends ModalWindow {

    public static ObjectProperty<ESolution> solution = new SimpleObjectProperty<>();


    public ESolution create(Draft draft, String status) {

        try {
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/utils-pics/Pikovka(256x256).png"));
            stage.setTitle("Внимание!");
            FXMLLoader userDialogLoader = new FXMLLoader(Draft_DuplicateDraftFound.class.getResource("/utils-fxml/drafts/duplicateDraftFound.fxml"));
            Parent parent = userDialogLoader.load();
            parent.getStylesheets().add(Draft_DuplicateDraftFound.class.getResource("/utils-css/pik-dark.css").toString());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Button btnShowDraft = (Button) parent.lookup("#btnShowDraft");
            btnShowDraft.setOnAction((event -> {
                Platform.runLater(()->{
                    new Draft_OutstandingPreviewer().create(stage, draft);
                });

            }));

            Button btnDelete = (Button) parent.lookup("#btnDelete");
            btnDelete.setOnAction((event -> {
                solution.set(ESolution.DELETE);
                WinformStatic.closeWindow(event);

            }));

            Button btnChange = (Button) parent.lookup("#btnChange");
            btnChange.setOnAction((event -> {
                solution.set(ESolution.CHANGE);
                WinformStatic.closeWindow(event);

            }));

            Button btnCancel = (Button) parent.lookup("#btnCancel");
            btnCancel.setOnAction((event -> {
                solution.set(ESolution.CANCEL);
                WinformStatic.closeWindow(event);

            }));

            Label lblTitle = (Label) parent.lookup("#lblWarningTitle");
            lblTitle.setText("ВНИМАНИЕ!");
            lblTitle.setStyle("-fx-text-fill: #fcce45");

            Label lblProblem = (Label) parent.lookup("#lblProblem");
            lblProblem.setText("Обнаружен " + status + " чертеж\n" +
                    draft.toUsefulString() + ",\n" +
                    "из комплекта " + draft.getFolder().toUsefulString());
            lblProblem.setStyle("-fx-text-fill: #fcce45");

            Label lblDecision = (Label) parent.lookup("#lblDecision");
            lblDecision.setText("Что с ним делать?");

            ModalWindow.setMovingPane(parent);

            Platform.runLater(() -> {
                ModalWindow.centerWindow(stage, WF_MAIN_STAGE, null);
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return solution.get();

    }

}

