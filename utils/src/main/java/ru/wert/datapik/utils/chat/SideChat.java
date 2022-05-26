package ru.wert.datapik.utils.chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;


public class SideChat {

    @Getter private Parent sideChatTalk;
    @Getter private Parent sideChatGroups;
    @Getter private AnchorPane chatMainPane;

    public SideChat() {

        chatMainPane = new AnchorPane();
        AppStatic.setNodeInAnchorPane(chatMainPane);

        createSideChatTalk();

        chatMainPane.getChildren().add(sideChatTalk);
    }

    private void createSideChatTalk(){
        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/sideChatTalk.fxml"));
            sideChatTalk = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
