package ru.wert.tubus.chogori.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.Getter;
import ru.wert.tubus.chogori.application.drafts.OpenDraftsEditorTask;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.images.BtnImages;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;

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
            lvCurrentDialog = new ListViewDialog(room, taMessageText);

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

        createBtnSendText();

        createBtnPictures();

        createBtnDrafts();

        createBtnRooms();

        createTextArea();


    }

    /**
     * ОТПРАВИТЬ ТЕКСТ
     */
    private void createBtnSendText() {
        btnSend.setText(null);
        btnSend.setGraphic(new ImageView(BtnImages.SEND_MESSAGE_IMG));
        btnSend.setOnAction(e->{
            lvCurrentDialog.sendText();
        });
    }

    /**
     * ОТПРАВИТЬ КАРТИНКУ
     */
    private void createBtnPictures() {
        btnAddPicture.setText("");
        btnAddPicture.setGraphic(new ImageView(BtnImages.BTN_ADD_CHAT_PIC_IMG));
        btnAddPicture.setTooltip(new Tooltip("Добавить изображение"));
        btnAddPicture.setOnAction(e->{
            lvCurrentDialog.sendPicture(e);
        });
    }

    /**
     * ОТКРЫТЬ ЧЕРТЕЖИ
     */
    private void createBtnDrafts() {
        btnAddDraft.setText("");
        btnAddDraft.setGraphic(new ImageView(BtnImages.BTN_ADD_CHAT_DRAFT_IMG));
        btnAddDraft.setTooltip(new Tooltip("Добавить чертеж"));
        btnAddDraft.setOnAction(e->{
            Thread t = new Thread(new OpenDraftsEditorTask());
            t.setDaemon(true);
            t.start();
        });
    }

    /**
     * ОТКРЫТЬ КОМНАТЫ
     */
    private void createBtnRooms() {
        btnRooms.setOnAction(e->{
            chat.showChatGroups();
        });
    }

    /**
     * ТЕКСТОВОЕ ПОЛЕ
     */
    private void createTextArea() {
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
        taMessageText.setOnKeyPressed(e -> {
            if (taMessageText.isFocused() &&
                    e.isControlDown() &&
                    e.getCode().equals(KeyCode.ENTER))
                btnSend.fire();
        });
    }


}
