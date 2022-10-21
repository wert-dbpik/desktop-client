package ru.wert.datapik.chogori.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.Getter;
import ru.wert.datapik.chogori.application.drafts.OpenDraftsEditorTask;
import ru.wert.datapik.client.entity.models.Message;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.chogori.images.ImageUtil;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Room;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.datapik.chogori.images.BtnImages.*;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideRoomDialogController {

    @FXML
    private StackPane spMessageContainer;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button btnRooms;

    @FXML
    @Getter
    private Label lblRoom;

    @FXML
    private TextArea taMessageText;

    @FXML
    private StackPane spDialogsContainer;

//    @FXML
//    private ListView<Message> currentDialog;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnAddPicture;

    @FXML
    private Button btnAddDraft;


    private SideChat chat;

    public static final float MESSAGE_WIDTH = 0.7f;
    public static final float PORTRAIT_WIDTH = 0.5f;
    public static final float LANDSCAPE_WIDTH = 0.7f;
    public static final float SQUARE_WIDTH = 0.6f;

    //Переменные для taMessageText
    private Text textHolder = new Text();
    private double oldHeight = 0;

    private ObservableList<Message> roomMessages;
    private Room room;
    private ListViewDialog lvCurrentDialog;
//    private List<Room> roomsInContainer;

    public void init(SideChat chat){
        this.chat = chat;
    }

    public void openDialog(Room room){
        this.room = room;
        ListViewDialog foundDialog = findOpenDialog(room);
        //Если диалог еще не открыт, то загружаем диалог в ноую комнату
        if(foundDialog == null) {
            lvCurrentDialog = new ListViewDialog(room);

            List<Message> messages = CH_MESSAGES.findAllByRoom(room);
            roomMessages =
                    messages == null ?
                            FXCollections.observableArrayList() : //пустой лист
                            FXCollections.observableArrayList(messages);
            lvCurrentDialog.setItems(roomMessages);
            lvCurrentDialog.setCellFactory((ListView<Message> tv) -> new ChatListCell());
            lvCurrentDialog.setId("listViewWithMessages");
            new ListViewWithMessages_Manipulator(lvCurrentDialog, this);
            spDialogsContainer.getChildren().add(lvCurrentDialog);

        }
        else
            lvCurrentDialog = foundDialog;

        lblRoom.setText(ChatMaster.getRoomName(room.getName()));
        lvCurrentDialog.toFront();

    }

    private ListViewDialog findOpenDialog(Room room) {
        ListViewDialog dialog = null;
        for(Node lvd : spDialogsContainer.getChildren() ){
            if(lvd instanceof ListViewDialog) {
                if (((ListViewDialog) lvd).getRoom().equals(room))
                    dialog = (ListViewDialog) lvd;
            }
        }
        return dialog;
    }

    @FXML
    void initialize(){
        
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

        btnRooms.setOnAction(e->{
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

    //============          ОТПРАВИТЬ ПАССПОРТА   ========================================================

    /**
     * Метод создает сообщение с пасспортами
     */
    public void createPassportsChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("PP")) continue;
            else {
                String strId = s.replace("PP#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        Message message = createChatMessage(Message.MessageType.CHAT_PASSPORTS, text.toString().trim());
        taMessageText.setText("");
        int index = roomMessages.size();
        lvCurrentDialog.getItems().add(message);
        lvCurrentDialog.refresh();
        lvCurrentDialog.scrollTo(message);

    }

    //============          ОТПРАВИТЬ ЧЕРТЕЖИ   ========================================================

    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ ЧЕРТЕЖИ
     * При нажатии открывается вкладка "Чертежи"
     */
    private void sendDraft(ActionEvent actionEvent) {

        Thread t = new Thread(new OpenDraftsEditorTask());
        t.setDaemon(true);
        t.start();
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

        Message message = createChatMessage(Message.MessageType.CHAT_DRAFTS, text.toString().trim());
        taMessageText.setText("");
        int index = roomMessages.size();
        lvCurrentDialog.getItems().add(message);
        lvCurrentDialog.refresh();
        lvCurrentDialog.scrollTo(message);

    }

    /**
     * Метод создает сообщение с комплектами чертежей
     */
    public void createFoldersChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("F")) continue;
            else {
                String strId = s.replace("F#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        Message message = createChatMessage(Message.MessageType.CHAT_FOLDERS, text.toString().trim());
        taMessageText.setText("");
        int index = roomMessages.size();
        lvCurrentDialog.getItems().add(message);
        lvCurrentDialog.refresh();
        lvCurrentDialog.scrollTo(message);

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
            Image image = new Image(file.toURI().toString());
            Pic savedPic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(savedPic.getId());
            text.append(" ");
        }

        Message message = createChatMessage(Message.MessageType.CHAT_PICS, text.toString().trim());
        taMessageText.setText("");
        int index = roomMessages.size();
        lvCurrentDialog.getItems().add(message);
        lvCurrentDialog.refresh();
        lvCurrentDialog.scrollTo(message);

    }

    //============          ОТПРАВИТЬ ТЕКСТ   ========================================================


    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ
     * Эта кнопка отправляет только текстовые сообщения
     */
    private void send(ActionEvent actionEvent) {
        String text = taMessageText.getText();
        Message message = createChatMessage(Message.MessageType.CHAT_TEXT, text);
        taMessageText.setText("");
        int index = roomMessages.size();
        lvCurrentDialog.getItems().add(message);
        lvCurrentDialog.refresh();
        lvCurrentDialog.scrollTo(message);
    }

    //=====================    ОБЩИЕ МЕТОДЫ    =================================================


    /**
     * Обновление ListView
     */
//    private void updateListView(boolean saveListPosition) {
//
//        Platform.runLater(()->{
//            currentDialog.autosize();
//            currentDialog.getItems().clear();
//            currentDialog.setItems(messages);
//            currentDialog.refresh();
//            if(!saveListPosition)
//                currentDialog.scrollTo(messages.size()-1);
//        });
//
//    }

    /**
     * Метода создает сообщение Message
     * @param type EMessageType
     * @param text String
     */
    private Message createChatMessage(Message.MessageType type, String text){
        Message message = new Message();
        message.setType(type);
        message.setSender(CH_CURRENT_USER);
        message.setCreationTime(AppStatic.getCurrentTime());
        message.setText(text);

        return message;
    }


}
