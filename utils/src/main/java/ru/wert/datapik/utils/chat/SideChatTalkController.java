package ru.wert.datapik.utils.chat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.utils.images.FileImage;
import ru.wert.datapik.utils.images.MessageContainer;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.File;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ADD_CHAT_PIC_IMG;
import static ru.wert.datapik.utils.images.BtnImages.SEND_MESSAGE_IMG;

public class SideChatTalkController {

    @FXML
    private StackPane spMessageContainer;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox vbPictures;

    @FXML
    private Button btnAddPicture;

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

    //Переменные для taText
    private Text textHolder = new Text();
    private double oldHeight = 0;


    @FXML
    void initialize(){
        btnSend.setText(null);
        btnSend.setGraphic(new ImageView(SEND_MESSAGE_IMG));

        btnAddPicture.setText("");
        btnAddPicture.setGraphic(new ImageView(BTN_ADD_CHAT_PIC_IMG));
        btnAddPicture.setTooltip(new Tooltip("Добавить изображение"));

        btnChatGroups.setOnAction(e->{
            chat.showChatGroups();
        });

        SplitPane.Divider divider = splitPane.getDividers().get(0);


        //Применим CSS стили к TextArea
        taMessageText.setId("blobTextArea");
        //Сделаем из стандартного TextArea раздуваемый
        textHolder.textProperty().bind(taMessageText.accessibleTextProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                double newHeight = textHolder.getLayoutBounds().getHeight()+30;
                taMessageText.setPrefHeight(newHeight);
                taMessageText.setMinHeight(newHeight);
                taMessageText.setMaxHeight(newHeight);

                divider.setPosition(1 - newHeight / splitPane.getHeight());
            }
        });

    }



    public void init(SideChat chat){
        this.chat = chat;

    }
}
