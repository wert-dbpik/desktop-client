package ru.wert.tubus.winform.warnings;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.tubus.winform.modal.ModalWindow;

import java.io.IOException;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class DialogCopyString extends ModalWindow {

    public static void create(String text){


        try {
            Stage stage = new Stage();
            FXMLLoader userDialogLoader = new FXMLLoader(DialogCopyString.class.getResource("/winform-fxml/warnings/dialogCopyString.fxml"));
            Parent parent = userDialogLoader.load();
            parent.getStylesheets().add(Warning2.class.getResource("/chogori-css/pik-dark.css").toString());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            Button btnClose = (Button)parent.lookup("#btnClose");
            btnClose.setStyle("-fx-background-color: rgb(50, 50, 50)");
            Image BTN_CLOSE_WHITE_IMG = new Image(DialogCopyString.class.getResourceAsStream("/chogori-pics/btns/close-white.png"), 16.0, 16.0, true, true);
            btnClose.setGraphic(new ImageView(BTN_CLOSE_WHITE_IMG));
            btnClose.setText("");
            btnClose.setOnAction((event -> {
                ((Node)event.getSource()).getScene().getWindow().hide();

            }));

            TextArea taTextArea = (TextArea)parent.lookup("#taText");
            taTextArea.setText(text);

            ModalWindow.setMovingPane(parent);

            stage.setAlwaysOnTop(true);


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
