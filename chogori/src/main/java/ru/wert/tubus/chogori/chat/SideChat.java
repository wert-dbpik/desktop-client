package ru.wert.tubus.chogori.chat;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.chogori.chat.socketwork.PushNotification;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Room;

import java.io.IOException;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_CHAT;

@Slf4j
public class SideChat {

    @Getter
    private Parent chatDialog;

    @Getter
    private Parent chatRooms;

    @Getter
    private DialogController dialogController;

    @Getter
    private RoomsController roomsController;

    @Getter
    private final VBox chatVBox;

    @Getter
    private final StackPane mainChatPane;

    private double mouseXStart, mouseXCurrent;
    private double spChatCurrentWidth;

    public SideChat() {
        log.info("Инициализация SideChat");

        AppStatic.SIDE_CHAT = this;

        PushNotification.init(this);

        createDialog();
        createRooms();

        chatVBox = new VBox();
        chatVBox.setFillWidth(true);

        // Создание разделителя с возможностью изменения ширины
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> ((Node) event.getSource()).setCursor(Cursor.H_RESIZE));
        separator.addEventHandler(MouseEvent.MOUSE_EXITED, event -> ((Node) event.getSource()).setCursor(Cursor.DEFAULT));
        separator.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            spChatCurrentWidth = SP_CHAT.getWidth();
            log.debug("Начало изменения ширины чата. Текущая ширина: {}", spChatCurrentWidth);
        });
        separator.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, event -> {
            double width = SP_CHAT.getWidth();
            SP_CHAT.setPrefWidth(width - event.getX());
            SP_CHAT.setMinWidth(width - event.getX());
            SP_CHAT.setMaxWidth(width - event.getX());
            event.consume();
        });

        HBox hbox = new HBox(separator);
        hbox.setFillHeight(true);
        chatVBox.getChildren().add(hbox);
        VBox.setVgrow(hbox, Priority.ALWAYS);

        AppStatic.setNodeInAnchorPane(chatVBox);

        mainChatPane = new StackPane();

        // Сначала открываются группы
        showChatGroups();

        hbox.getChildren().add(mainChatPane);
        HBox.setHgrow(mainChatPane, Priority.ALWAYS);
    }

    /**
     * Отображает панель с группами чатов.
     */
    public void showChatGroups() {
        log.debug("Отображение панели с группами чатов");
        mainChatPane.getChildren().clear();
        mainChatPane.getChildren().add(chatRooms);
        roomsController.getListOfRooms().refresh();

    }

    /**
     * Отображает диалог чата для указанной комнаты.
     *
     * @param room Комната, для которой нужно открыть диалог.
     */
    public void showChatDialog(Room room) {
        log.debug("Открытие диалога чата для комнаты: {}", room.getName());
        dialogController.getLblRoom().setText(ChatStaticMaster.getRoomName(room.getName()));

        dialogController.openDialogForRoom(room);
        mainChatPane.getChildren().clear();
        mainChatPane.getChildren().add(chatDialog);
    }

    /**
     * Создает панель для отображения диалога чата.
     */
    private void createDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/dialog.fxml"));
            chatDialog = loader.load();
            dialogController = loader.getController();
            dialogController.init(this);
            log.debug("Панель для диалога чата успешно создана");
        } catch (IOException e) {
            log.error("Ошибка при создании панели для диалога чата", e);
            e.printStackTrace();
        }
    }

    /**
     * Создает панель для отображения комнат.
     */
    private void createRooms() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/rooms.fxml"));
            chatRooms = loader.load();
            roomsController = loader.getController();
            roomsController.init(this);
            log.debug("Панель для комнат успешно создана");
        } catch (IOException e) {
            log.error("Ошибка при создании панели для комнат", e);
            e.printStackTrace();
        }
    }


}
