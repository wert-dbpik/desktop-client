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
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.chogori.statics.UtilStaticNodes;
import ru.wert.datapik.chogori.toolpane.ChogoriToolBar;
import ru.wert.datapik.chogori.common.components.FileFwdSlash;
import ru.wert.datapik.chogori.tempfile.TempDir;
import ru.wert.datapik.winform.statics.WinformStatic;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;
import ru.wert.datapik.winform.window_decoration.WindowDecorationController;
import ru.wert.datapik.winform.winform_settings.WinformSettings;

import java.io.IOException;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;
import static ru.wert.datapik.chogori.images.AppImages.LOGO_ICON;
import static ru.wert.datapik.chogori.images.AppImages.LOGO_IMG;
import static ru.wert.datapik.winform.statics.WinformStatic.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ATTENTION;

@Slf4j
public class StartChogori extends Application {

    private boolean initStatus = true;

    @Override
    public void init(){

        try {
            initServices();
            initQuickServices();
            log.debug("init : DATA from server got well!");
        } catch (Exception e) {
            log.error("init : couldn't get DATA from server");
            initStatus = false;

        }

        //Определяем последнюю доступную версию программы в Базе данных
        List<VersionDesktop> allVersions = CH_VERSIONS_DESKTOP.findAll();
        WinformStatic.LAST_VERSION_IN_DB = allVersions.get(allVersions.size()-1).getName();

        new ChogoriToolBar();
        //Создадим папку временного хранения файлов чертежей
        FileFwdSlash tempDir = TempDir.createTempDirectory("temp-baza-pik");
        log.info("Temp folder has been created : {}", tempDir.toString());

        WinformStatic.WF_TEMPDIR = tempDir;
        log.info("WinformStatic.WF_TEMPDIR = tempDir; passed" );
        WinformSettings.CH_MONITOR = AppProperties.getInstance().getMonitor();
        log.info("AppProperties.getInstance().getMonitor() passed");

    }


    @Override
    public void start(Stage stage) throws Exception {

        if (!initStatus) {
            Warning1.create($ATTENTION, "Не удалось загрузить чертежи с сервера", "Работа программы будет прекращена" +
                    "\nдля перезагрузки сервера обратитесь к администратору");
            System.exit(0);
        }
        WinformStatic.WF_MAIN_STAGE = stage;

        try {
            //Загружаем WindowDecoration
            FXMLLoader decorationLoader = new FXMLLoader(WindowDecoration.class.getResource("/winform-fxml/window_decoration/window_decoration.fxml"));
            Parent decoration = decorationLoader.load();
            WindowDecorationController controller = decorationLoader.getController();

            //Загружаем loginWindow
            FXMLLoader loginWindowLoader = new FXMLLoader(getClass().getResource("/chogori-fxml/login/login.fxml"));
            Parent loginWindow = loginWindowLoader.load();

            //loginWindow помещаем в WindowDecoration
            UtilStaticNodes.CH_DECORATION_ROOT_PANEL = (StackPane)decoration.lookup("#mainPane");
            UtilStaticNodes.CH_DECORATION_ROOT_PANEL.getChildren().add(loginWindow);


            //Меняем заголовок окна
            Label programName = (Label)decoration.lookup("#programName");
            Label labelVersion = (Label)decoration.lookup("#lblVersion");
            Label windowName = (Label)decoration.lookup("#windowName");

            programName.setText(!TEST_VERSION ? PROGRAM_NAME : PROGRAM_NAME + " ТЕСТ");
            windowName.setText("");

            Scene scene = new Scene(decoration);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            scene.getStylesheets().add(this.getClass().getResource("/chogori-css/pik-dark.css").toString());


            stage.sizeToScene();
            stage.setResizable(true);
            stage.getIcons().add(LOGO_ICON);
//            stage.setTitle("База ПИК");


            stage.show();
            controller.centerInitialWindow(stage, true, WinformSettings.CH_MONITOR);


        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        LauncherImpl.launchApplication(StartChogori.class, AppPreloader.class, args);
    }
}
