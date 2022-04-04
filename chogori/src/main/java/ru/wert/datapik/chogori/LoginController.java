package ru.wert.datapik.chogori;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.utils.common.components.BXUsers;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_SETTINGS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_DECORATION_ROOT_PANEL;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;


@Slf4j
public class LoginController {

    @FXML
    private ComboBox<User> bxUsers;

    //Поле ввода пароля
    @FXML
    private PasswordField passwordField;

    public static final User admin = CH_USERS.findById(1L);

    @FXML
    void initialize() {

        log.debug("initialize : login window is initializing ...");
        new BXUsers(bxUsers);

        long userId = AppProperties.getInstance().getLastUser();
        User lastUser = userId == 0L ? null : CH_USERS.findById(userId);
        bxUsers.getSelectionModel().select(lastUser);

        CH_CURRENT_USER = null;
        CH_CURRENT_USER_GROUP = null;

        passwordField.setOnAction(e->{
            try {
                welcomeUser();
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        });

        Platform.runLater(()->passwordField.requestFocus());

        log.debug("initialize : login window has been initialized well!");

    }

    private void welcomeUser() throws NoSuchFieldException{
        User user = bxUsers.getValue();
        if(user == null) user = admin;

        String pass = passwordField.getText();
        //Вход без регистрации личности
        // 1 - Используется зарезервированный пароль разработчика
        if(user == admin & pass.equals(CH_DEV_PASS)){
            CH_CURRENT_USER = admin;
            loadApplicationSettings();
            showTabPaneWindow();
            openTestWindow();
        }
        //Вход с регистрацией личности
        else {
            if(user.getPassword().equals(pass)){
                CH_CURRENT_USER = user;
                loadApplicationSettings();
                showTabPaneWindow();
                openTestWindow();
            } else
                Warning1.create($ATTENTION, $NO_SUCH_USER, $TRY_MORE);
        }
        AppProperties.getInstance().setLastUser(user.getId());
    }

    private void loadApplicationSettings() {
        log.debug("loadApplicationSettings : application settings are going to be load");
        CH_CURRENT_USER_GROUP = CH_CURRENT_USER.getUserGroup();

        CH_CURRENT_USER_SETTINGS = CH_SETTINGS.findByName(CH_CURRENT_USER.getName());
        if(CH_CURRENT_USER_SETTINGS == null){
            AppSettings defaultSettings = CH_SETTINGS.findByName("default");
            AppSettings newUserSettings = defaultSettings.makeCopy();
            newUserSettings.setName(CH_CURRENT_USER.getName());
            newUserSettings.setUser(CH_CURRENT_USER);
            CH_CURRENT_USER_SETTINGS = CH_SETTINGS.save(newUserSettings);
        }

        CH_SHOW_PREFIX = CH_CURRENT_USER_SETTINGS.isShowPrefixes();
        CH_DEFAULT_PREFIX = CH_CURRENT_USER_SETTINGS.getDefaultPrefix();
        CH_VALIDATE_DEC_NUMBERS = CH_CURRENT_USER_SETTINGS.isValidateDecNumbers();
        CH_PDF_VIEWER = EPDFViewer.values()[CH_CURRENT_USER_SETTINGS.getPdfViewer()];
        CH_DEFAULT_PATH_TO_NORMY_MK = new File(CH_CURRENT_USER_SETTINGS.getPathToNormyMK());

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
