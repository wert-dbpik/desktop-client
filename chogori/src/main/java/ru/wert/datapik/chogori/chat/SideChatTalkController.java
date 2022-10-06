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
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.chogori.images.ImageUtil;
import ru.wert.datapik.chogori.statics.AppStatic;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_QUICK_DRAFTS;
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

    }

    @FXML
    void initialize(){
        messages = FXCollections.observableArrayList();
        listViewWithMessages.setCellFactory((ListView<ChatMessage> tv) -> new ChatListCell());
        listViewWithMessages.setId("listViewWithMessages");
        listViewWithMessages.setItems(messages);

        new ListViewWithMessages_Manipulator(listViewWithMessages, this);

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

    //============          ОТПРАВИТЬ ЧЕРТЕЖИ   ========================================================

    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ ЧЕРТЕЖИ
     * При нажатии открывается вкладка "Чертежи"
     */
    private void sendDraft(ActionEvent actionEvent) {
        AppMenuController.openDrafts(actionEvent);
    }

    /**
     * Метод создает сообщение с чертежами
     */
    public void createDraftsChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("DR")) continue;
            else {
                String strId = s.replace("DR#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        ChatMessage message = createChatMessage(EMessageType.CHAT_DRAFTS, text.toString().trim());
        taMessageText.setText("");
        int index = messages.size();
        listViewWithMessages.getItems().add(message);
        listViewWithMessages.refresh();
        listViewWithMessages.scrollTo(message);

    }

    //============          ОТПРАВИТЬ ИЗОБРАЖЕНИЯ   ========================================================

    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ ИЗОБРАЖЕНИЯ
     * При нажатии открывается вкладка "Чертежи"
     */
    private void sendPicture(ActionEvent event) {
        StringBuilder text = new StringBuilder();
        // Пользователь выбирает несколько рисунков
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;

        createPicsChatMessage(chosenFiles);
    }

    /**
     * Метод создает сообщение с изображениями
     * @param chosenFiles
     */
    public void createPicsChatMessage( List<File> chosenFiles) {
        StringBuilder text = new StringBuilder();
        for(File file : chosenFiles){
//            ImageView imageView = ImageUtil.createImageViewFromFile(file, null);
            Image image = new Image(file.toURI().toString());
            Pic savedPic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(savedPic.getId());
            text.append(" ");
        }

        ChatMessage message = createChatMessage(EMessageType.CHAT_PICS, text.toString().trim());
        taMessageText.setText("");
        int index = messages.size();
        listViewWithMessages.getItems().add(message);
        listViewWithMessages.refresh();
        listViewWithMessages.scrollTo(message);

    }

    //============          ОТПРАВИТЬ ТЕКСТ   ========================================================


    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ
     * Эта кнопка отправляет только текстовые сообщения
     */
    private void send(ActionEvent actionEvent) {
        String text = taMessageText.getText();
        ChatMessage message = createChatMessage(EMessageType.CHAT_TEXT, text);
        taMessageText.setText("");
        int index = messages.size();
        listViewWithMessages.getItems().add(message);
        listViewWithMessages.refresh();
        listViewWithMessages.scrollTo(message);
    }

    //=====================    ОБЩИЕ МЕТОДЫ    =================================================


    /**
     * Обновление ListView
     */
//    private void updateListView(boolean saveListPosition) {
//
//        Platform.runLater(()->{
//            listViewWithMessages.autosize();
//            listViewWithMessages.getItems().clear();
//            listViewWithMessages.setItems(messages);
//            listViewWithMessages.refresh();
//            if(!saveListPosition)
//                listViewWithMessages.scrollTo(messages.size()-1);
//        });
//
//    }

    /**
     * Метода создает сообщение ChatMessage
     * @param type EMessageType
     * @param text String
     */
    private ChatMessage createChatMessage(EMessageType type, String text){
        ChatMessage message = new ChatMessage();
        message.setMessageType(type.ordinal());
        message.setUser(CH_CURRENT_USER);
        message.setCreationTime(AppStatic.getCurrentTime());
        message.setText(text);

        return message;
    }


}
