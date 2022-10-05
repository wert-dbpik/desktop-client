package ru.wert.datapik.chogori.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.Getter;
import ru.wert.datapik.chogori.application.app_window.AppMenuController;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.chogori.images.ImageUtil;
import ru.wert.datapik.chogori.statics.AppStatic;

import java.io.File;
import java.util.List;

import static ru.wert.datapik.chogori.images.BtnImages.*;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideChatTalkController {

    @FXML
    private StackPane spMessageContainer;

    @FXML
    private SplitPane splitPane;

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

    @FXML
    private Button btnAddPicture;

    @FXML
    private Button btnAddDraft;


    private SideChat chat;

    public static final float MESSAGE_WIDTH = 0.8f;
    public static final float PORTRAIT_WIDTH = 0.5f;
    public static final float LANDSCAPE_WIDTH = 0.8f;
    public static final float SQUARE_WIDTH = 0.6f;

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
        btnAddPicture.setOnAction(this::sendPicture);

        btnAddDraft.setText("");
        btnAddDraft.setGraphic(new ImageView(BTN_ADD_CHAT_DRAFT_IMG));
        btnAddDraft.setTooltip(new Tooltip("Добавить чертеж"));
        btnAddDraft.setOnAction(this::sendDraft);

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

    private void sendDraft(ActionEvent actionEvent) {
        AppMenuController.openDrafts(actionEvent);
    }

    private void sendPicture(ActionEvent event) {
        StringBuilder text = new StringBuilder();
        // Пользователь выбирает несколько рисунков
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;
        for(File file : chosenFiles){
//            ImageView imageView = ImageUtil.createImageViewFromFile(file, null);
            Image image = new Image(file.toURI().toString());
            Pic savedPic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(savedPic.getId());
            text.append(" ");
        }

        ChatMessage message = createChatMessage(EMessageType.CHAT_PICS, text.toString().trim());
        messages.add(message);
        updateListView();
        taMessageText.setText("");
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
