package ru.wert.datapik.chogori.application.app_window;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.StartChogori;
import ru.wert.datapik.utils.tabs.MainTabPane;
import ru.wert.datapik.utils.toolpane.ChogoriToolBar;

import java.io.IOException;
import java.util.ArrayList;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

public class ApplicationController {

    @FXML
    private VBox vbApplication;

    @FXML
    private StackPane rootPanel;

    @FXML
    private Label lblUser;

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
        createTabPane();
        createButtonInterceptor();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/appWindow/appMenu.fxml"));
            Parent parent = loader.load();
            spAppMenu.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Перехватчик нажатых клавиш
     * При нажатии, клавиша сохраняется в CH_KEYS_NOW_PRESSED,
     * при отпускании, клавиша удаляется из CH_KEYS_NOW_PRESSED
     */
    private void createButtonInterceptor() {
        CH_KEYS_NOW_PRESSED = new ArrayList<>();

        CH_MAIN_STAGE.getScene().setOnKeyPressed((e)->{
            CH_KEYS_NOW_PRESSED.add(e.getCode());
        });

        CH_MAIN_STAGE.getScene().setOnKeyReleased((e)->{
            CH_KEYS_NOW_PRESSED.remove(e.getCode());
        });
    }

    private void createTabPane(){
        CH_TAB_PANE = new MainTabPane();
        rootPanel.getChildren().add(CH_TAB_PANE);
    }



    private void createUserLabel(){
        if (CH_CURRENT_USER_GROUP != null)
            lblUser.setText(CH_CURRENT_USER.getName() + ", " + CH_CURRENT_USER_GROUP.getName());
        else
            lblUser.setText(CH_CURRENT_USER.getName());
    }
}
