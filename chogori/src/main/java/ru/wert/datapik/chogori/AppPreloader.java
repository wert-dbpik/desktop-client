package ru.wert.datapik.chogori;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.chogori.connectionProperties.NoConnection;
import ru.wert.datapik.winform.modal.ModalWindow;

@Slf4j
public class AppPreloader extends Preloader {

    private Stage preloaderWindow = null;

    @Override
    public void start(Stage preloaderWindow) {
        log.debug("start : preloaderWindow is starting ...");
        this.preloaderWindow = preloaderWindow;
        preloaderWindow.initStyle(StageStyle.UNDECORATED);
        try {
            RetrofitClient.getInstance();
            if(!RetrofitClient.checkUpConnection())
                showSetConnectionDialog(preloaderWindow);
        } catch (Exception e) {
            showSetConnectionDialog(preloaderWindow);
        }

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.BOTTOM_CENTER);

        vBox.getChildren().add(new Label("Идет загрузка данных ..."));

        BorderPane root = new BorderPane(vBox);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/chogori-css/preloader.css").toString());
        preloaderWindow.setScene(scene);

        preloaderWindow.getIcons().add(new Image("/chogori-pics/Pikovka(256x256).png"));
        log.debug("start : preloaderWindow icon has been loaded");

        preloaderWindow.setTitle("Загрузка данных");
        preloaderWindow.show();

        ModalWindow.mountStage(preloaderWindow, AppProperties.getInstance().getMonitor());

        log.debug("start : preloaderWindow has been deployed and centered well!");
    }

    private void showSetConnectionDialog(Stage preloaderWindow) {
        boolean r = new NoConnection(preloaderWindow).create();
        preloaderWindow.hide();
        if (r) start(preloaderWindow);
        else System.exit(0);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START){
            preloaderWindow.hide();
        }
    }

}
