package ru.wert.datapik.winform.modal;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;


//import static ru.wert.datapik.utils._patches.common.components.Nodes.MAIN_WINDOW;


public class ModalWindow {

    protected static double dragOffsetX;
    protected static double dragOffsetY;


// =======================   ПЕРЕМЕЩАЕМ ОКНО  ========================================

    protected static void onMousePressed(MouseEvent mouseEvent){
        Stage window = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        dragOffsetX = mouseEvent.getScreenX() - window.getX();
        dragOffsetY = mouseEvent.getScreenY() - window.getY();
    }

    protected static void onMouseDragged(MouseEvent mouseEvent) {
        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        window.setX(mouseEvent.getScreenX() - dragOffsetX);
        window.setY(mouseEvent.getScreenY() - dragOffsetY);
    }
//====================================================================================

    protected static void setMovingPane(Parent parent) {
        AnchorPane anchorPane = (AnchorPane)parent.lookup("#movingPane");
        anchorPane.setOnMousePressed((ModalWindow::onMousePressed));
        anchorPane.setOnMouseDragged((ModalWindow::onMouseDragged));
    }

    /**
     * Метод центрирует модальное окно относительно главного окна приложения
     * @param stage сцена модального окна
     */
    public static void centerWindow(Stage stage){

        WindowDecoration.centerWindow(stage, WF_MAIN_STAGE, false);

//        ObservableList<Screen> screens = Screen.getScreens();
//        double mainX = stage.getScene().getWindow().getX() + stage.getScene().getWidth()/2.0; //MAIN_WINDOW = stage
//        for(Screen s : screens){
//            Rectangle2D screenRec = s.getBounds();
//            if(mainX >= screenRec.getMinX() && mainX <= screenRec.getMaxX()){
//                double modWidth = stage.getScene().getWindow().getWidth();
//                //Устанавливаем положение модального окна по вычисленной координате Х
//                stage.setX(s.getVisualBounds().getMinX() + (s.getVisualBounds().getWidth() - modWidth)/2.0);
//            }
//        }
    }

    protected static void center2Window(Stage stage){
        ObservableList<Screen> screens = Screen.getScreens();
        double mainX = stage.getScene().getWindow().getX() + stage.getScene().getWidth()/2.0; //MAIN_WINDOW = stage
        for(Screen s : screens){
            Rectangle2D screenRec = s.getBounds();
            if(mainX >= screenRec.getMinX() && mainX <= screenRec.getMaxX()){
                double modWidth = stage.getScene().getWindow().getWidth();
                //Устанавливаем положение модального окна по вычисленной координате Х
                stage.setX(s.getVisualBounds().getMinX() + (s.getVisualBounds().getWidth() - modWidth)/2.0);
            }

        }
    }

}
