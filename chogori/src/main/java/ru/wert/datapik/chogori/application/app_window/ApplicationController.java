package ru.wert.datapik.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.UtilStaticNodes;
import ru.wert.datapik.utils.tabs.MainTabPane;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class ApplicationController {

    @FXML
    private VBox vbApplication;

    @FXML
    private StackPane rootPanel;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblTime;

   @FXML
    private StackPane spAdvert;

    @FXML
    private StackPane stackPaneForToolPane;

    @FXML
    private StackPane spAppMenu;


    @FXML
    void initialize() {
//        CH_APPLICATION_ROOT_PANEL = rootPanel;
        CH_TOOL_STACK_PANE = stackPaneForToolPane;

//        createToolPanel();
        createUserLabel();
        createTimeLabel();
        createTabPane();
        createButtonInterceptor();

        if(AppStatic.CURRENT_PROJECT_VERSION.compareTo(AppStatic.LAST_VERSION_IN_DB) < 0)
            createAdvertLabel();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/appWindow/appMenu.fxml"));
            Parent parent = loader.load();
            spAppMenu.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UtilStaticNodes.CH_DECORATION_ROOT_PANEL.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                if (content.size() > 1) return;
                if (content.get(0).getName().endsWith(".xlsx")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                } else
                    event.acceptTransferModes(TransferMode.NONE);
                event.consume();
            }
        });

        UtilStaticNodes.CH_DECORATION_ROOT_PANEL.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if(db.hasFiles() && event.getTransferMode().equals(TransferMode.MOVE)){
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                if(content.get(0).getName().endsWith(".xlsx")) {
                    Task<Void> openExcelFile = new TaskOpenExcelFile(content.get(0));

                    Thread t = new Thread(openExcelFile);
                    t.setDaemon(true);
                    t.start();
                }
            }
            event.consume();
        });

    }


    /**
     * Перехватчик нажатых клавиш
     * При нажатии, клавиша сохраняется в CH_KEYS_NOW_PRESSED,
     * при отпускании, клавиша удаляется из CH_KEYS_NOW_PRESSED
     */
    private void createButtonInterceptor() {
        CH_KEYS_NOW_PRESSED = new ArrayList<>();

        WF_MAIN_STAGE.getScene().setOnKeyPressed((e)->{
            CH_KEYS_NOW_PRESSED.add(e.getCode());
        });

        WF_MAIN_STAGE.getScene().setOnKeyReleased((e)->{
            CH_KEYS_NOW_PRESSED.remove(e.getCode());
        });
    }

    private void createTabPane(){
        CH_TAB_PANE = new MainTabPane();
        rootPanel.getChildren().add(CH_TAB_PANE);
    }



    private void createUserLabel(){
        if (CH_CURRENT_USER_GROUP != null)
            lblUser.setText(CH_CURRENT_USER.getName());
    }

    private void createTimeLabel(){

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String time = (LocalTime.now()).format(DateTimeFormatter.ofPattern("HH:mm"));
                Platform.runLater(()->{
                    lblTime.setText(time);
                });

            }
        }, 0, 1000);

    }

    private void createAdvertLabel(){
        Label lblNewVersion = new Label();
        lblNewVersion.setStyle("-fx-text-fill: #FFFF99; -fx-background-color: -fx-my-black;");
        lblNewVersion.setText("Доступна новая версия v." + AppStatic.LAST_VERSION_IN_DB);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(lblNewVersion.isVisible())
                    lblNewVersion.setVisible(false);
                else
                    lblNewVersion.setVisible(true);
            }
        }, 0, 500);
        spAdvert.getChildren().add(lblNewVersion);
        spAdvert.setOnMouseClicked((event)->{
            spAdvert.getChildren().clear();
            new TaskDownloadNewVersion();
        });

    }

}
