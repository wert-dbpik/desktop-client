package ru.wert.datapik.winform.statics;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.winform.warnings.Warning2;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
public class WinformStatic {

    public static Stage CH_MAIN_STAGE;
    public static File CH_TEMPDIR; //Директория временного хранения

    /**
     * Метод обеспечивает закрытие любого окна
     * В случае, если это окно главного приложения, то осуществляется выход из приложения
     */
    public static void closeWindow(Event event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        if (window.equals(WinformStatic.CH_MAIN_STAGE)) {
            exitApplication(event);
        } else
            window.hide();
    }

    /**
     * Метод обеспечивает выход из приложения
     */
    public static void exitApplication(Event event){
        if(Warning2.create("ОДУМАЙТЕСЬ!", "Вы близки к выходу из программы?", "А поработать?")) {
            clearCash();
            System.exit(0);
        }
    }

    /**
     * Метод удаляет все файлы из папки, где кэшируются данные
     */
    public static void clearCash() {
        if (CH_TEMPDIR.exists()) {
            for (File file : CH_TEMPDIR.listFiles()) {
                if (file.isFile())
                    file.delete();
            }
            log.debug("AppStatic : folder with cash has been cleared");
        }
    }

    public static void centerWindow(Stage window, Boolean fullScreen, int mainMonitor){

        List<Screen> screenList = Screen.getScreens();
        //Если всего один монитор, то открываем на нем
        int monitor = Math.min(mainMonitor, screenList.size() - 1);

        if(fullScreen) {
            window.setWidth(screenList.get(monitor).getBounds().getWidth());
            window.setHeight(screenList.get(monitor).getBounds().getHeight());
        }
        double screenMinX = screenList.get(monitor).getBounds().getMinX();
        double screenMinY = screenList.get(monitor).getBounds().getMinY();
        double screenWidth = screenList.get(monitor).getBounds().getWidth();
        double screenHeight = screenList.get(monitor).getBounds().getHeight();

        window.setX(screenMinX + ((screenWidth - window.getWidth()) / 2));
        window.setY(screenMinY + ((screenHeight - window.getHeight()) / 2));

    }

//    public static void setNodeInAnchorPane(Node node){
//        AnchorPane.setBottomAnchor(node, 0.0);
//        AnchorPane.setRightAnchor(node, 0.0);
//        AnchorPane.setLeftAnchor(node, 0.0);
//        AnchorPane.setTopAnchor(node, 0.0);
//    }

public static String parseLDTtoNormalDate(String localDateTime){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    LocalDateTime ldt = LocalDateTime.parse(localDateTime);
    return ldt.format(formatter);
}

}
