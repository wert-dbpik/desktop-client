package ru.wert.datapik.start;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.serviceREST.UserService;
import ru.wert.datapik.viewer.StartViewer;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.viewer.application.main.ViewerServices;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.viewer.application.main.CurrentSettings.*;
import static ru.wert.datapik.viewer.application.main.ViewerServices.USERS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;


public class StartHere extends Application {

    private PasswordField passwordField;

    @Override
    public void init() throws IOException {
        ViewerServices.USERS = UserService.getInstance();
//        AppSettings defaultSettings = Services.SETTINGS.findByName("default");
//        while(true) {
//            if(defaultSettings != null) {
//                CurrentAppSettings.MONITOR = defaultSettings.getMonitor();
//                break;
//            }
//        }
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(StartHere.class, AppPreloader.class, args);
    }


    @Override
    public void start(Stage loginWindow) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start-fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        loginWindow.setScene(scene);

        passwordField = (PasswordField)root.lookup("#pasFldPassword");
        Button btnCancel = (Button)root.lookup("#btnCancel");
        Button btnEnter = (Button)root.lookup("#btnEnter");

        btnCancel.setOnAction(this::cancel);
        btnEnter.setOnAction(this::enter);

        loginWindow.setResizable(false);
        loginWindow.initStyle(StageStyle.UNDECORATED);
        loginWindow.show();
        AppStatic.centerWindow(loginWindow, false, MONITOR);
    }


    /**
     * Метод закрывает окно при нажатии на ОТМЕНА
     * @param event Event
     */
    void cancel(Event event){
        AppStatic.closeWindow(event);
    }

    /**
     * Метод обрабатывает нажатие кнопки ВОЙТИ
     * @param event Event
     */
    void enter(Event event){
        String pass = passwordField.getText();
        //Вход без регистрации личности
        // 1 - Используется зарезервированный пароль разработчика
        if(pass.equals(DEV_PASS)){
            CURRENT_USER = new User();
            CURRENT_USER.setName("Разработчик");
            CURRENT_USER_GROUP = CURRENT_USER.getUserGroup();

            openApplication(event);
        }
        //Вход с регистрацией личности
        else {
            User user = USERS.findByPassword(pass);
            if(user != null){
                CURRENT_USER = user;
                CURRENT_USER_GROUP = user.getUserGroup();

                openApplication(event);
            } else
                Warning1.create($ATTENTION, $NO_SUCH_USER, $TRY_MORE);
        }

    }

    private void openApplication(Event event){
        AppStatic.closeWindow(event);


                String[] args = new String[0];

        final String javaHome = System.getProperty("java.home");
        final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        final String classpath = System.getProperty("java.class.path");
        final Class<StartViewer> klass = StartViewer.class;
        final String className = klass.getCanonicalName();
        final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
            try {
                Process process = builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
