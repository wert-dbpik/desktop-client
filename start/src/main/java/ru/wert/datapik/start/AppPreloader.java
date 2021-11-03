package ru.wert.datapik.start;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.utils.statics.AppStatic;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_MONITOR;

public class AppPreloader extends Preloader {

    private Stage preloaderWindow = null;

    @Override
    public void start(Stage preloaderWindow) {
        this.preloaderWindow = preloaderWindow;
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.BOTTOM_CENTER);

        vBox.getChildren().add(new Label("Идет загрузка данных ..."));

        BorderPane root = new BorderPane(vBox);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/start-css/preloader.css").toString());
        preloaderWindow.setScene(scene);
        preloaderWindow.initStyle(StageStyle.UNDECORATED);
        preloaderWindow.show();
        AppStatic.centerWindow(preloaderWindow, false, CH_MONITOR);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START){
            preloaderWindow.hide();
        }
    }

}
