package ru.wert.datapik.utils.chat;

import com.sun.prism.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.ChatMessage;

import static ru.wert.datapik.utils.images.BtnImages.SEND_MESSAGE_IMG;

public class SideChatTalkController {


    @FXML
    private Button btnChatGroups;

    @FXML
    @Getter
    private Label lblChatGroup;

    @FXML
    private TextArea taMessageText;

    @FXML
    private ListView<ChatMessage> listOfMessages;

    @FXML
    private Button btnSend;
    private SideChat chat;

    @FXML
    void initialize(){
        btnSend.setText(null);
        btnSend.setGraphic(new ImageView(SEND_MESSAGE_IMG));

        btnChatGroups.setOnAction(e->{
            chat.showChatGroups();
        });
    }

    public void init(SideChat chat){
        this.chat = chat;
    }
}
