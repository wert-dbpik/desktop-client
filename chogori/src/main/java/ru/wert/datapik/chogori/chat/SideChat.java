package ru.wert.datapik.chogori.chat;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Room;
import ru.wert.datapik.chogori.statics.AppStatic;

import java.io.IOException;

import static ru.wert.datapik.chogori.statics.UtilStaticNodes.SP_CHAT;


public class SideChat {

    @Getter private Parent sideChatTalk;

    @Getter private Parent sideChatGroups;
    @Getter private SideChatDialogController talkController;
    @Getter private SideRoomsController groupsController;
    @Getter private final VBox chatVBox;
    @Getter private final StackPane mainPane;


    private double mouseXStart, mouseXCurrent;
    private double spChatCurrentWidth;


    public SideChat() {

        createSideChatTalk();
        createSideChatGroups();
        chatVBox = new VBox();
        chatVBox.setFillWidth(true);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> ((Node)event.getSource()).setCursor(Cursor.H_RESIZE));
        separator.addEventHandler(MouseEvent.MOUSE_EXITED, event -> ((Node)event.getSource()).setCursor(Cursor.DEFAULT));
        separator.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> spChatCurrentWidth = SP_CHAT.getWidth());
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

        mainPane = new StackPane();

        //Сначала открываются группы
        showChatGroups();

        hbox.getChildren().add(mainPane);
        HBox.setHgrow(mainPane, Priority.ALWAYS);

    }

    public void showChatGroups(){
        mainPane.getChildren().clear();
        mainPane.getChildren().add(sideChatGroups);
    }

    public void showChatTalk(Room room){
        talkController.getLblRoom().setText(ChatMaster.getRoomName(room.getName()));
        mainPane.getChildren().clear();
        mainPane.getChildren().add(sideChatTalk);
    }

    private void createSideChatTalk(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/sideChatDialog.fxml"));
            sideChatTalk = loader.load();
            talkController = loader.getController();
            talkController.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSideChatGroups(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/sideRooms.fxml"));
            sideChatGroups = loader.load();
            groupsController = loader.getController();
            groupsController.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
