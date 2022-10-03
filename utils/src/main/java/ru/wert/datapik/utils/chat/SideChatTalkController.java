package ru.wert.datapik.utils.chat;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.utils.images.FileImage;
import ru.wert.datapik.utils.images.MessageContainer;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.views.lists.SimpleListCell;

import java.io.File;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ADD_CHAT_PIC_IMG;
import static ru.wert.datapik.utils.images.BtnImages.SEND_MESSAGE_IMG;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideChatTalkController {

    @FXML
    private StackPane spMessageContainer;

    @FXML
    private SplitPane splitPane;

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
    private ListView<ChatMessage> listViewWithMessages;

    @FXML
    private Button btnSend;


    private SideChat chat;

    //Переменные для taMessageText
    private Text textHolder = new Text();
    private double oldHeight = 0;

    private ObservableList<ChatMessage> messages;

    public void init(SideChat chat){
        this.chat = chat;
        messages = FXCollections.observableArrayList();

    }

    @FXML
    void initialize(){
        listViewWithMessages.setCellFactory((ListView<ChatMessage> tv) -> new ChatListCell());
        listViewWithMessages.setId("listViewWithMessages");

        btnSend.setText(null);
        btnSend.setGraphic(new ImageView(SEND_MESSAGE_IMG));
        btnSend.setOnAction(this::send);

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
        textHolder.textProperty().bind(taMessageText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth()-10);
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();

                double newHeight = textHolder.getLayoutBounds().getHeight()+30;
                divider.setPosition(1.0 - newHeight / splitPane.getHeight());
                taMessageText.resize(taMessageText.getWidth(),newHeight);
            }
        });

        taMessageText.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth()-10);
            double newHeight = textHolder.getLayoutBounds().getHeight()+30;
            divider.setPosition(1.0 - newHeight / splitPane.getHeight());
            taMessageText.resize(taMessageText.getWidth(), newHeight);
        });


    }

    private void send(ActionEvent actionEvent) {
        String text = taMessageText.getText();
        ChatMessage message = createChatMessage(EMessageType.CHAT_TEXT, text);
        messages.add(message);
        updateListView();
        taMessageText.setText("");

    }

    private void updateListView() {
        Platform.runLater(()->{
            listViewWithMessages.autosize();
            listViewWithMessages.getItems().clear();
            listViewWithMessages.getItems().addAll(messages);
            listViewWithMessages.refresh();
        });

    }


    private ChatMessage createChatMessage(EMessageType type, String text){
        ChatMessage message = new ChatMessage();
        message.setMessageType(type.ordinal());
        message.setUser(CH_CURRENT_USER);
        message.setCreationTime(AppStatic.getCurrentTime());
        message.setText(text);

        return message;
    }
}
