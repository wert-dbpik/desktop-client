package ru.wert.datapik.winform.window_decoration;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Setter;
import ru.wert.datapik.winform.modal.ModalWindow;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.util.List;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

//@Slf4j
public class WindowDecorationController {

    @FXML private ImageView imgBtnClose;
    @FXML private ImageView imgBtnMaximize;
    @FXML private ImageView imgBtnMinimize;
    @FXML private Label windowName;
    @FXML private StackPane stackPane;
    @FXML private VBox vBox;
    @FXML private Pane paneR;
    @FXML private Pane paneX;
    @FXML private Pane paneB;

    private Stage window;
    private double dragOffsetX;
    private double dragOffsetY;
    @Setter private boolean isExpanded;

    private double windowCurrentWidth;
    private double windowCurrentHeight;


//===========================================    НАЧАЛО     ========================================================
    /**
     *  Конструктор без параметров
     *  Обязателен для корректной работы с FXML файлом
     */
    public WindowDecorationController() {

    }

//===========================================    КНОПКИ     ========================================================

    /**
     * Обработка события кнопки "_" на заголовке окна
     * Сворачивание окна при нажатии на кнопку
     */
    @FXML
    void minimizeWindow(Event e){
        ((Stage) ((Node)e.getSource()).getScene().getWindow()).setIconified(true);
    }

    /**
     * Обработка события кнопки "+" на заголовке окна
     * Разворачивание окна на весь экран, если оно еще не развернуто
     * или сворчивание окна до предыдущего состояния
     */
    @FXML
    void maximizeWindow(MouseEvent e){
        window = (Stage) ((Node)e.getSource()).getScene().getWindow();
        changeSizeOfWindow(window, e);
    }

    /**
     * Закрытие окна при нажатии на Х или на кнопку ОТМЕНА
     * Метод закрывает окно принажатии на крестик или на кнопку ОТМЕНА
     * Так же метод может быть вызван из контроллеров
     */
    @FXML
    public void closeWindow(Event event){

        WinformStatic.closeWindow(event);

    }

//===========================================    СОБЫТИЯ МЫШИ     =====================================================

    /**
     * Обработка двойного нажатия мыши на заголовок окна
     * Метод, необходим для разворачивания окна на весь экран
     * И обратного сворачивания
     * @param mouseEvent MouseEvent
     */
    @FXML
    private void TitleBarOnMouseClicked(MouseEvent mouseEvent){

        Stage window = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        this.window = window;

        if (window.isFocused() &&
                window.isResizable() &&
                mouseEvent.getClickCount() == 2)
                    changeSizeOfWindow(window, mouseEvent);
    }

    /**
     * Разворачивание или сворачивание окна
     * Условием является состояние флага isExpanded (развернут на весь экран)
     */
    private void changeSizeOfWindow(Stage window, MouseEvent e){
        List<Screen> screenList = Screen.getScreens();
        int monitor = ModalWindow.findCurrentMonitorByMousePointer(e);

        ((Node)e.getSource()).getScene().getWindow();

        Rectangle2D visualBounds = findVisualBounds();

        if (isExpanded) {
            if(window.equals(WF_MAIN_STAGE)){
                //Меняем ширину и высоту окна, если она равна или чуть меньше размеров самого экрана
                if (windowCurrentWidth < 0.95 * screenList.get(monitor).getBounds().getWidth())
                    windowCurrentWidth = 0.7 * screenList.get(monitor).getBounds().getWidth();

                if (windowCurrentHeight < 0.95 * screenList.get(monitor).getBounds().getHeight())
                    windowCurrentHeight = 0.7 * screenList.get(monitor).getBounds().getHeight();
            }

            window.setWidth(windowCurrentWidth);
            window.setHeight(windowCurrentHeight);
            window.setY((visualBounds.getHeight() - windowCurrentHeight)/2);

            ModalWindow.centerWindow(window, WF_MAIN_STAGE, e);
            isExpanded = false;
        } else {
            this.windowCurrentWidth = window.getWidth();
            this.windowCurrentHeight = window.getHeight();

            setWindowToFullScreen(window);
            isExpanded = true;
        }
    }

    /**
     * Разворачивает окно на весь экран с учетом видимой области
     */
    private void setWindowToFullScreen(Stage window){

        List<Screen> screenList = Screen.getScreens();
        int monitor = ModalWindow.findCurrentMonitorByMainStage(window);
        Rectangle2D visualBounds = screenList.get(monitor).getVisualBounds();

        window.setX(visualBounds.getMinX());
        window.setY(visualBounds.getMinY());
        window.setWidth(visualBounds.getWidth());
        window.setHeight(visualBounds.getHeight());
    }

    /**
     * Определяет видимые границы экрана
     * @return Rectangle2D
     */
    private Rectangle2D findVisualBounds() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        List<Screen> screenList = Screen.getScreens();
        for (Screen screen : screenList) {
            double x = window.getX();
            if ((x >= screen.getBounds().getMinX()) && (x < screen.getBounds().getMaxX() - 1))
                visualBounds = screen.getVisualBounds();
        }
        return visualBounds;
    }
//===========================================  РАСТЯГИВАЕМ ОКНО   =================================================

    /**
     * Изменить курсор с дефолтного на соответствующий направлению растягивания
     * В модель окна добавлены вспомогательные панели, три из которых имеют названия
     * paneX - панель на перекрестии панель правой(Right) и нижней (BottomLineController)
     * Курсор меняется только для resizable окон
     * @param e MouseEvent
     */
    @FXML
    private void changeCursorOnMouseMoved(MouseEvent e){
        Stage window = (Stage) ((Node)e.getSource()).getScene().getWindow();

        if (window.isResizable()) {
            switch(((Node)e.getSource()).getId()){
                case ("paneR"): ((Node) e.getSource()).setCursor(Cursor.H_RESIZE); break;
                case ("paneB"): ((Node) e.getSource()).setCursor(Cursor.V_RESIZE); break;
                case ("paneX"): ((Node) e.getSource()).setCursor(Cursor.NW_RESIZE); break;
            }
        }
    }

    /**
     * Изменить размер экрана при перетаскивании его краев
     * Изменение размеров окна возможно только для resizable окон. Размер окна в итоге получается
     * "приблизительно", т.к. толщину вспомогательных панелей и куда ткунались для перетаскивания мышка
     * никто не учитывает. Единственно что, если край таскать за минимальный размер окна, то по факту окно
     * окончательно принимает минимальный размер. Использование метода OnMousePressed дало бы более точный результат
     * но мне кажется, что он работает медленнее.
     * @param e
     */
    @FXML
    private void resizeWindowOnMouseDragged(MouseEvent e){
        Stage window = (Stage) ((Node)e.getSource()).getScene().getWindow();
        if (window.isResizable()) {
                double newWidth = e.getScreenX()-window.getX();
                double newHeight = e.getScreenY()-window.getY();

                switch(((Node) e.getSource()).getId()){
                    case ("paneR"): {
                        window.setWidth(Math.max(newWidth, window.getMinWidth()));
                    } break;
                    case ("paneB"): {
                        window.setHeight(Math.max(newHeight, window.getMinHeight()));
                    } break;
                    case ("paneX"): {
                        window.setWidth(Math.max(newWidth, window.getMinWidth()));
                        window.setHeight(Math.max(newHeight, window.getMinHeight()));
                    }; break;
            }
        }

    }

//==========================================   ПЕРЕМЕЩАЕМ ОКНО  ===================================================
    /**
     * Обработка нажатия мыши на заголовок окна
     * Метод, необходимы для перетаскивания окна за его загловок.
     * Здесь определяется положение мыши в относительных координатах.
     * @param mouseEvent MouseEvent
     */
    @FXML
    private void onMousePressed(MouseEvent mouseEvent){
        Stage window = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        this.dragOffsetX = mouseEvent.getScreenX() - window.getX();
        this.dragOffsetY = mouseEvent.getScreenY() - window.getY();
    }

    /**
     * Обработка движения мыши приперетаскивании окна
     * Метод, необходимы для перетаскивания окна за его загловок.
     * Здесь окну передаются координаты, которые он меняет при перетаскивании
     * @param mouseEvent
     */
    @FXML
    private void onMouseDragged(MouseEvent mouseEvent){
        Stage window = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        window.toFront();
        window.setX(mouseEvent.getScreenX() - this.dragOffsetX);
        window.setY(mouseEvent.getScreenY() - this.dragOffsetY);
    }

    public void centerInitialWindow(Stage window, Boolean fullScreen, int mainMonitor){

        List<Screen> screenList = Screen.getScreens();
        //Если всего один монитор, то открываем на нем
        int monitor = Math.min(mainMonitor, screenList.size() - 1);

        if(fullScreen) {
            window.setWidth(screenList.get(monitor).getBounds().getWidth());
            window.setHeight(screenList.get(monitor).getBounds().getHeight());
            setExpanded(true);
        }
        ModalWindow.mountStage(window, monitor);

    }


//===========================================    СОБЫТИЯ КЛАВИАТУРЫ     ===============================================
    /**
     * Закрытие окна при нажатии на клавишу ESCAPE
     * @param keyEvent
     */
    @FXML
    void OnKeyEscapeTyped(KeyEvent keyEvent){
        Stage window = (Stage) ((Node)keyEvent.getSource()).getScene().getWindow();
        if (window.isFocused() &&
            keyEvent.getCode() == KeyCode.ESCAPE)
            window.hide();
    }
//=================================================   ГЕТТЕРЫ   =====================================================
    protected Stage getWindow(){
        return window;
    }

    public Pane getPaneR(){
        return paneR;
    }

}

