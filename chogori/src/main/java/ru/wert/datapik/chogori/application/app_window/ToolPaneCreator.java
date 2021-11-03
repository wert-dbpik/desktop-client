package ru.wert.datapik.chogori.application.app_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ToolPaneCreator {

    public static HBox create(){
        try {
            FXMLLoader loader = new FXMLLoader(ToolPaneCreator.class.getResource("/chogori-fxml/appWindow/toolPane.fxml"));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
