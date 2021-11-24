package ru.wert.datapik.chogori;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.chogori.application.app_window.AppMenuController;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.utils.help.About;
import ru.wert.datapik.utils.statics.UtilStaticNodes;
import ru.wert.datapik.utils.services.ChogoriServices;
import ru.wert.datapik.utils.setteings.ChogoriSettings;
import ru.wert.datapik.utils.toolpane.ChogoriToolBar;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.utils.common.components.FileFwdSlash;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.tempfile.TempDir;
import ru.wert.datapik.winform.statics.WinformStatic;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_MONITOR;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ATTENTION;

@Slf4j
public class StartChogori extends Application {

    private Parent applicationPane;
    public static AppMenuController CH_MENU_CONTROLLER;
    private boolean initStatus = true;

    @Override
    public void init(){


        try {
            initServices();
            initQuickServices();
        } catch (Exception e) {
            log.error("init : не удалось загрузить данные с сервера");
            initStatus = false;

        }

        //Определяем версию программы
        findVersions();

        new ChogoriToolBar();
        //Создадим папку временного хранения файлов чертежей
        FileFwdSlash tempDir = TempDir.createTempDirectory("bddrafts");
        log.info("Cоздана временная папка : {}", tempDir.toString());
        //Зашружаем свойства программы
        AppSettings defaultSettings = ChogoriServices.CH_SETTINGS.findByName("default");
        while(true) {
            if(defaultSettings != null) {
                ChogoriSettings.CH_TEMPDIR = tempDir;
                CH_MONITOR = defaultSettings.getMonitor();
                break;
            }
        }
    }

    /**
     * Метод определяет текущую и последние версии
     */
    private void findVersions() {
        Package aPackage = About.class.getPackage();
        AppStatic.CURRENT_ROJECT_VERSION = aPackage.getImplementationVersion();
        if(AppStatic.CURRENT_ROJECT_VERSION != null) {
            VersionDesktop currentVersion = CH_VERSIONS_DESKTOP.findByName(AppStatic.CURRENT_ROJECT_VERSION);
            List<VersionDesktop> versions = CH_VERSIONS_DESKTOP.findAll();
            VersionDesktop lastVersion = versions.get(versions.size() - 1);
            int comp = currentVersion.compareTo(lastVersion);
            if(comp < 0)
                AppStatic.NEWER_PROJECT_VERSION = lastVersion.getName();
            else
                AppStatic.NEWER_PROJECT_VERSION = null;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        if (!initStatus) {
            Warning1.create($ATTENTION, "Не удалось загрузить чертежи с сервера", "Работа программы будет прекращена" +
                    "\nдля перезагрузки сервера обратитесь к администратору");
            System.exit(0);
        }
        WinformStatic.CH_MAIN_STAGE = stage;
        try {
            //Загружаем WindowDecoration
            FXMLLoader decorationLoader = new FXMLLoader(WindowDecoration.class.getResource("/winform-fxml/window_decoration/window_decoration.fxml"));
            Parent decoration = decorationLoader.load();

            //Загружаем loginWindow
            FXMLLoader loginWindowLoader = new FXMLLoader(getClass().getResource("/chogori-fxml/login/login.fxml"));
            Parent loginWindow = loginWindowLoader.load();

            //loginWindow помещаем в WindowDecoration
            UtilStaticNodes.CH_DECORATION_ROOT_PANEL = (StackPane)decoration.lookup("#mainPane");
            UtilStaticNodes.CH_DECORATION_ROOT_PANEL.getChildren().add(loginWindow);


            //Меняем заголовок окна
            Label windowName = (Label)decoration.lookup("#windowName");
            String headerName = "Информационная база";
            windowName.setText(headerName);

            Scene scene = new Scene(decoration);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            scene.getStylesheets().add(this.getClass().getResource("/utils-css/pik-dark.css").toString());


            stage.sizeToScene();
            stage.setResizable(true);
            stage.getIcons().add(new Image("/utils-pics/Pikovka(256x256).png"));
            stage.setTitle("База ПИК");

            //Временная строка
//            CurrentUser.getInstance().currentUserProperty().set(UserService.getInstance().findById(1L));
//            TabManager.getInstance();

//            setOnKeysPressedListener();

            stage.show();
            AppStatic.centerWindow(stage, true, CH_MONITOR);


        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        LauncherImpl.launchApplication(StartChogori.class, AppPreloader.class, args);
    }
}
