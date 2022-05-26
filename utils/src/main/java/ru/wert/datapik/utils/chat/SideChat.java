package ru.wert.datapik.utils.chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;


public class SideChat {

    @Getter private Parent sideChatTalk;

    @Getter private Parent sideChatGroups;
    @Getter private SideChatTalkController talkController;
    @Getter private SideChatGroupsController groupsController;
    @Getter private VBox chatMainPane;

    public SideChat() {
        createSideChatTalk();
        createSideChatGroups();
        chatMainPane = new VBox();
        chatMainPane.setFillWidth(true);
        AppStatic.setNodeInAnchorPane(chatMainPane);
        chatMainPane.getChildren().add(sideChatTalk);
        VBox.setVgrow(sideChatTalk, Priority.ALWAYS);
    }

    private void createSideChatTalk(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/sideChatTalk.fxml"));
            sideChatTalk = loader.load();
            talkController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSideChatGroups(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/sideChatGroups.fxml"));
            sideChatGroups = loader.load();
            groupsController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
