package ru.wert.datapik.chogori.common.components;

import javafx.scene.control.ComboBox;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class BXMonitor {

    public List<String> create(ComboBox<String> bxMonitor){
        List<Screen> screenList = Screen.getScreens();
        List<String> screens = new ArrayList<>();
        for(int i = 0; i < screenList.size(); i++){
            String width = String.valueOf(screenList.get(i).getVisualBounds().getWidth());
            String height = String.valueOf(screenList.get(i).getVisualBounds().getHeight());
            if(i == 0) screens.add(i+1 + ": " + width + " x " + height + " (основной)");
            else screens.add(i+1 + ": " + width + " x " + height);
        }
        bxMonitor.getItems().addAll(screens);
        return screens;
    }
}
