package ru.wert.datapik.chogori;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.StartChogori;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.statics.WinformStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_SETTINGS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;


@Slf4j
public class LoginController {

    //Поле ввода пароля
    @FXML
    private PasswordField passwordField;


    @FXML
    void initialize() {

        CH_CURRENT_USER = null;
        CH_CURRENT_USER_GROUP = null;

        passwordField.setOnAction(e->{
            try {
                welcomeUser();
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        });
        

    }

    private void welcomeUser() throws NoSuchFieldException {
        String pass = passwordField.getText();
        //Вход без регистрации личности
        // 1 - Используется зарезервированный пароль разработчика
        if(pass.equals(CH_DEV_PASS)){
            CH_CURRENT_USER = CH_USERS.findById(1L);
            CH_CURRENT_USER_GROUP = CH_CURRENT_USER.getUserGroup();
            CH_CURRENT_USER_SETTINGS = CH_SETTINGS.findByName(CH_CURRENT_USER.getName());
            if(CH_CURRENT_USER_SETTINGS == null){
                AppSettings newSettings = CH_SETTINGS.findByName("default");
                newSettings.setName(CH_CURRENT_USER.getName());
                newSettings.setUser(CH_CURRENT_USER);
                CH_CURRENT_USER_SETTINGS = CH_SETTINGS.save(newSettings);
            }

            { //Вытаскиваем настройки программы
                CH_SHOW_PREFIX = CH_CURRENT_USER_SETTINGS.isShowPrefixes();
                CH_DEFAULT_PREFIX = CH_CURRENT_USER_SETTINGS.getDefaultPrefix();
                CH_VALIDATE_DEC_NUMBERS = CH_CURRENT_USER_SETTINGS.isValidateDecNumbers();
                CH_PDF_VIEWER = EPDFViewer.values()[CH_CURRENT_USER_SETTINGS.getPdfViewer()];
                CH_DEFAULT_PATH_TO_NORMY_MK = new File(CH_CURRENT_USER_SETTINGS.getPathToNormyMK());
            }

            showTabPaneWindow();
        }
        //Вход с регистрацией личности
        else {
            User user = CH_USERS.findByPassword(pass);
            if(user != null){
                CH_CURRENT_USER = user;
                CH_CURRENT_USER_GROUP = user.getUserGroup();

                showTabPaneWindow();

                openTestWindow();

            } else
                Warning1.create($ATTENTION, $NO_SUCH_USER, $TRY_MORE);
        }

    }

    private void openTestWindow(){
        //========================================================================================
//        Platform.runLater(()->{
//            StartChogori.CH_MENU_CONTROLLER.openCatalogOfMaterials(new ActionEvent());
//        });


//===========================================================================================================
    }


    private void showTabPaneWindow() {
        log.info("В приложение вошел по паролю {} в качестве {}", CH_CURRENT_USER.getName(), CH_CURRENT_USER_GROUP);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/appWindow/application.fxml"));
            Parent parent = loader.load();

            CH_DECORATION_ROOT_PANEL.getChildren().clear();
            CH_DECORATION_ROOT_PANEL.getChildren().add(parent);


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
