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
import retrofit2.Retrofit;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.utils.connectionProperties.NoConnection;
import ru.wert.datapik.utils.statics.AppStatic;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_MONITOR;


public class AppPreloader extends Preloader {

    private Stage preloaderWindow = null;

    @Override
    public void start(Stage preloaderWindow) {
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

        preloaderWindow.getIcons().add(new Image("/utils-pics/Pikovka(256x256).png"));
        preloaderWindow.setTitle("Загрузка данных");
        preloaderWindow.show();
        AppStatic.centerWindow(preloaderWindow, false, CH_MONITOR);
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
