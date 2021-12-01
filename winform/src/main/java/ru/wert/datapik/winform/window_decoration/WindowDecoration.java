package ru.wert.datapik.winform.window_decoration;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.util.List;

@Slf4j
public class WindowDecoration {
    private Stage window = null;
    private final String headerName; //название окна
    private FXMLLoader decorationLoader;
    private final Parent rootPane;
    private Parent decoration;
    private Label windowName;
    private final boolean resizable;
    private boolean waiting = false;


//=============================================    НАЧАЛО    =========================================================
    /**
     * Конструктор
     * 1 - С указанием владельца
     */
    public WindowDecoration(String headerName, Parent rootPane, Boolean resizable, Stage owner){
        this.headerName = headerName;
        this.rootPane = rootPane;
        this.waiting = waiting;
        this.resizable = resizable;
        log.debug("{} создан", this.getClass().getSimpleName());
        createWindow(owner);
    }

    /**
     * Окно для ожидания ответа
     * @param headerName
     * @param rootPane
     * @param resizable
     * @param owner
     * @param waiting - Ожидание ответа
     */
    public WindowDecoration(String headerName, Parent rootPane, Boolean resizable, Stage owner, boolean waiting ){
        this.headerName = headerName;
        this.rootPane = rootPane;
        this.waiting = waiting;
        this.resizable = resizable;
        log.debug("{} создан", this.getClass().getSimpleName());
        createWindow(owner);
        mountResizeButtons();

    }


    /**
     * создание окна
     * Окно имеет переменную главную панель размещенную на StackPane. Подгружаются два fxml файла:
     * 1) - Панель с заголовком окна
     * 2) - Собственно переменная панель
     * Для обращения к элементам панели с заголовкам окна используется метод lookup().
     * Центрирование окна происходит после метода show()
     */
    private void createWindow(Stage owner){
        try {
            decorationLoader = new FXMLLoader(getClass().getResource("/winform-fxml/window_decoration/window_decoration.fxml"));
            decoration = decorationLoader.load();

            StackPane pane = (StackPane)decoration.lookup("#mainPane");
            pane.getChildren().add(rootPane);

            //Меняем заголовок окна
            windowName = (Label)decoration.lookup("#windowName");
            windowName.setText(headerName);

            Scene scene = new Scene(decoration);
            scene.getStylesheets().add(this.getClass().getResource("/utils-css/pik-dark.css").toString());;

            window = new Stage();
            window.setScene(scene);

            window.initStyle(StageStyle.UNDECORATED);
            if (owner != null) settingOwner(owner);

            window.sizeToScene();
            window.setResizable(this.resizable);


            if(!waiting) window.show();

            Platform.runLater(()->{
                window.setMinWidth(window.getWidth());
                window.setMinHeight(window.getHeight());
                centerWindow(window, owner, false);
                window.toFront();
            });


            if(waiting) window.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void settingOwner(Stage owner){
        window.initModality(Modality.WINDOW_MODAL);
        window.initOwner(owner);

    }

    private void mountResizeButtons(){
        //Для модальных окон скрываем кнопки свернуть/развернуть кнопки
        ImageView imgMinimizeWindow = (ImageView)decoration.lookup("#imgBtnMinimize");
        imgMinimizeWindow.setVisible(resizable);
        ImageView imgMaximizeWindow = (ImageView)decoration.lookup("#imgBtnMaximize");
        imgMaximizeWindow.setVisible(resizable);
    }

    /**
     * Центрирование окна
     * Так как штатное центрирование задирает окно на 1/3, то используется собственный способ
     * размещения окна на видимой части экрана. Панель задач внизу экрана вычитается.
     */
    public static void centerWindow(Stage window, Stage mainWindow, Boolean fullScreen){

        List<Screen> screenList = Screen.getScreens();
        //Если всего один монитор, то открываем на нем
        int monitor = screenList.size() - 1;

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

/*    *//**
     * Центрирование окна
     * Так как штатное центрирование задирает окно на 1/3, то используется собственный способ
     * размещения окна на видимой части экрана. Панель задач внизу экрана вычитается.
     *//*
    public void centerWindow(Stage window){

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - window.getWidth())/2.0;
        double y = bounds.getMinY() + (bounds.getHeight() - window.getHeight())/2.0;
        window.setX(x);
        window.setY(y);
    }*/

    public FXMLLoader getDecorationLoader(){
        return decorationLoader;
    }

    public Stage getWindow(){
        return window;
    }

    public Label getWindowName(){
        return windowName;
    }
}
