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
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Room;

import java.io.IOException;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_CHAT;

@Slf4j
public class SideChat {

    @Getter
    private Parent sideChatTalk;

    @Getter
    private Parent sideChatGroups;

    @Getter
    private SideRoomDialogController talkController;

    @Getter
    private SideRoomsController groupsController;

    @Getter
    private final VBox chatVBox;

    @Getter
    private final StackPane mainPane;

    private double mouseXStart, mouseXCurrent;
    private double spChatCurrentWidth;

    public SideChat() {
        log.info("Инициализация SideChat");

        createSideChatTalk();
        createSideChatGroups();

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
            log.debug("Изменение ширины чата. Новая ширина: {}", SP_CHAT.getWidth());
            event.consume();
        });

        HBox hbox = new HBox(separator);
        hbox.setFillHeight(true);
        chatVBox.getChildren().add(hbox);
        VBox.setVgrow(hbox, Priority.ALWAYS);

        AppStatic.setNodeInAnchorPane(chatVBox);

        mainPane = new StackPane();

        // Сначала открываются группы
        showChatGroups();

        hbox.getChildren().add(mainPane);
        HBox.setHgrow(mainPane, Priority.ALWAYS);
    }

    /**
     * Отображает панель с группами чатов.
     */
    public void showChatGroups() {
        log.debug("Отображение панели с группами чатов");
        mainPane.getChildren().clear();
        mainPane.getChildren().add(sideChatGroups);
    }

    /**
     * Отображает диалог чата для указанной комнаты.
     *
     * @param room Комната, для которой нужно открыть диалог.
     */
    public void showChatDialog(Room room) {
        log.debug("Открытие диалога чата для комнаты: {}", room.getName());
        talkController.getLblRoom().setText(ChatMaster.getRoomName(room.getName()));
        talkController.openDialogForRoom(room);
        mainPane.getChildren().clear();
        mainPane.getChildren().add(sideChatTalk);
    }

    /**
     * Создает панель для отображения диалога чата.
     */
    private void createSideChatTalk() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/sideRoomDialog.fxml"));
            sideChatTalk = loader.load();
            talkController = loader.getController();
            talkController.init(this);
            log.debug("Панель для диалога чата успешно создана");
        } catch (IOException e) {
            log.error("Ошибка при создании панели для диалога чата", e);
            e.printStackTrace();
        }
    }

    /**
     * Создает панель для отображения групп чатов.
     */
    private void createSideChatGroups() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/sideRooms.fxml"));
            sideChatGroups = loader.load();
            groupsController = loader.getController();
            groupsController.init(this);
            log.debug("Панель для групп чатов успешно создана");
        } catch (IOException e) {
            log.error("Ошибка при создании панели для групп чатов", e);
            e.printStackTrace();
        }
    }


}
