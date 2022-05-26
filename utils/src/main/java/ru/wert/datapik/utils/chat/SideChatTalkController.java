package ru.wert.datapik.utils.chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import ru.wert.datapik.client.entity.models.ChatMessage;

public class SideChatTalkController {


    @FXML
    private Button btnChatGroups;

    @FXML
    private Label lblChatGroup;

    @FXML
    private TextArea taMessageText;

    @FXML
    private ListView<ChatMessage> listOfMessages;

    @FXML
    private Button btnSend;

    void init(){

    }
}
