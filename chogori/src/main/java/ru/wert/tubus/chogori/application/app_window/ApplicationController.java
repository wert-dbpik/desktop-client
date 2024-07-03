package ru.wert.tubus.chogori.application.app_window;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.UtilStaticNodes;
import ru.wert.tubus.chogori.tabs.MainTabPane;
import ru.wert.tubus.winform.statics.WinformStatic;
import ru.wert.tubus.winform.winform_settings.WinformSettings;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ru.wert.tubus.chogori.images.BtnImages.BTN_UPDATE_BLUE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.*;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.*;
import static ru.wert.tubus.winform.statics.WinformStatic.CURRENT_PROJECT_VERSION;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
@Slf4j
public class ApplicationController {

    @FXML
    private VBox vbApplication;

    @FXML
    private HBox waitingBlind;

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
    private StackPane spChat;

    @FXML
    Button btnUpdateAllData;


    @FXML
    void initialize() {
        log.debug("initialize : запускается блок инициализации");
        CH_APPLICATION_ROOT_PANEL = rootPanel;
        CH_APPLICATION_WAITING_BLIND = waitingBlind;
        CH_TOOL_STACK_PANE = stackPaneForToolPane;

        CH_TOOL_STACK_PANE.setVisible(false);

        SP_CHAT = spChat;


        createUserLabel();
        createTimeLabel();
        createTabPane();
        createButtonInterceptor();

        if(compareVersions(CURRENT_PROJECT_VERSION, WinformStatic.LAST_VERSION_IN_DB ))
            createAdvertLabel();

//        if(CURRENT_PROJECT_VERSION.compareTo(WinformStatic.LAST_VERSION_IN_DB) < 0)
//            createAdvertLabel();

        try {
            log.debug("initialize : appMenu.fxml создается ...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/appWindow/appMenu.fxml"));
            Parent parent = loader.load();
            spAppMenu.getChildren().add(parent);
            log.debug("initialize : appMenu.fxml успешно создана и добавлена к  spAppMenu");

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


        btnUpdateAllData.setStyle("-fx-text-fill: #FFFFFF;");
        btnUpdateAllData.setPadding(Insets.EMPTY);
        btnUpdateAllData.setGraphic(new ImageView(BTN_UPDATE_BLUE_IMG));
        btnUpdateAllData.setText("Обновить данные!");
        btnUpdateAllData.setOnAction(e->AppMenuController.updateData());


        log.debug("initialize : блок инициализации успешно выполнен");

    }

    /**
     * Возвращает true если требуется запустить анонс новой программы,
     * т.е. currentProjectVersion < lastVersionInDb
     */
    boolean compareVersions(String currentProjectVersion, String lastVersionInDb) {
        String[] current = currentProjectVersion.split("\\.", -1);
        String[] last = lastVersionInDb.split("\\.", -1);

        for(int i = 0; i < 3 ; i++){
            int cV = current.length < i+1 ?
                            0 :
                            Integer.parseInt(current[i]);
            int lV = last.length < i+1 ?
                    0 :
                    Integer.parseInt(last[i]);
            if(cV < lV)
                return true;
        }
        return false;
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
        log.debug("createAdvertLabel : создается надпись с новой доступной версией программы");
        Label lblNewVersion = new Label();
        lblNewVersion.setStyle("-fx-text-fill: #FFFF99; -fx-background-color: -fx-my-black;");
        lblNewVersion.setText("Доступна новая версия v." + WinformStatic.LAST_VERSION_IN_DB);

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

        log.debug("createAdvertLabel : надпись с новой доступной версией программы успешно создана");

    }

}
