package ru.wert.datapik.utils.setteings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class SettingsWindow {

    public void show(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/settings/settings.fxml"));
            Parent parent = loader.load();

            new WindowDecoration("Настройки", parent, false, WF_MAIN_STAGE, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
