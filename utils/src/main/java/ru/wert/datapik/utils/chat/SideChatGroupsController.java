package ru.wert.datapik.utils.chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import ru.wert.datapik.client.entity.models.ChatGroup;

public class SideChatGroupsController {

    @FXML
    private ListView<ChatGroup> listOfChatGroups;

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private Button btnLastChat;

    void init(){

    }

}
