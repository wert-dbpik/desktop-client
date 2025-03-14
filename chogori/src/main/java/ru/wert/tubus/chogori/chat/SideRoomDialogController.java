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
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.images.BtnImages;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;

/**
 * Контроллер для управления диалогами в комнатах чата.
 * Отвечает за отображение сообщений, отправку текста, изображений и чертежей.
 */
public class SideRoomDialogController {

    @FXML
    private StackPane spMessageContainer; // Контейнер для текстового поля ввода сообщения (taMessageText)

    @FXML
    private SplitPane splitPane; // Разделитель для управления размерами элементов интерфейса

    @FXML
    private Button btnRooms; // Кнопка для открытия списка доступных комнат

    @FXML
    @Getter
    private Label lblRoom; // Название текущей комнаты

    @FXML
    private TextArea taMessageText; // Текстовое поле для ввода сообщений

    @FXML
    private StackPane spDialogsContainer; // Контейнер для отображения сообщений в текущей комнате

    @FXML
    private Button btnSend; // Кнопка для отправки текстового сообщения

    @FXML
    private Button btnAddPicture; // Кнопка для добавления изображения

    @FXML
    private Button btnAddDraft; // Кнопка для добавления чертежа

    private SideChat chat; // Ссылка на основной класс чата


    // Константы для управления размерами сообщений
    public static final float MESSAGE_WIDTH = 0.7f;
    public static final float PORTRAIT_WIDTH = 0.5f;
    public static final float LANDSCAPE_WIDTH = 0.7f;
    public static final float SQUARE_WIDTH = 0.6f;

    // Переменные для управления высотой текстового поля
    private Text textHolder = new Text();
    private double oldHeight = 0;

    private ObservableList<Message> roomMessages; // Список сообщений в текущей комнате
    private Room room; // Текущая комната
    private ListViewDialog lvCurrentDialog; // Текущий диалог (список сообщений)

    /**
     * Инициализация контроллера.
     * @param chat Ссылка на основной класс чата.
     */
    public void init(SideChat chat) {
        this.chat = chat;
        ServerMessageHandler.sideRoomDialogController = this;
    }

    /**
     * Открывает диалог для указанной комнаты.
     * Если диалог уже открыт, переключается на него, иначе создает новый.
     * @param room Комната, для которой нужно открыть диалог.
     */
    public void openDialogForRoom(Room room) {
        this.room = room;
        ListViewDialog foundDialog = findDialogForRoom(room);

        // Если диалог еще не открыт, создаем новый
        if (foundDialog == null) {
            lvCurrentDialog = new ListViewDialog(room, taMessageText);

            // Загружаем сообщения для комнаты
            List<Message> messages = CH_MESSAGES.findAllByRoom(room);
            roomMessages = messages == null ?
                    FXCollections.observableArrayList() : // Пустой список, если сообщений нет
                    FXCollections.observableArrayList(messages);

            // Настраиваем ListView для отображения сообщений
            lvCurrentDialog.setItems(roomMessages);
            lvCurrentDialog.setCellFactory((ListView<Message> tv) -> new ChatListCell());
            lvCurrentDialog.setId("listViewWithMessages");

            // Добавляем манипулятор для обработки событий
            new ListViewWithMessages_Manipulator(lvCurrentDialog, this);

            // Добавляем диалог в контейнер
            spDialogsContainer.getChildren().add(lvCurrentDialog);
        } else {
            lvCurrentDialog = foundDialog;
        }

        // Устанавливаем название комнаты и переключаемся на диалог
        lblRoom.setText(ChatMaster.getRoomName(room.getName()));
        lvCurrentDialog.toFront();
    }

    /**
     * Ищет открытый диалог для указанной комнаты.
     * @param room Комната, для которой ищется диалог.
     * @return Найденный диалог или null, если диалог не найден.
     */
    public ListViewDialog findDialogForRoom(Room room) {
        ListViewDialog dialog = null;
        for (Node lvd : spDialogsContainer.getChildren()) {
            if (lvd instanceof ListViewDialog) {
                if (((ListViewDialog) lvd).getRoom().equals(room))
                    dialog = (ListViewDialog) lvd;
            }
        }
        return dialog;
    }

    /**
     * Инициализация контроллера при загрузке FXML.
     */
    @FXML
    void initialize() {
        createBtnSendText(); // Настройка кнопки отправки текста
        createBtnPictures(); // Настройка кнопки добавления изображений
        createBtnDrafts();   // Настройка кнопки добавления чертежей
        createBtnRooms();    // Настройка кнопки открытия списка комнат
        createTextArea();    // Настройка текстового поля ввода
    }

    /**
     * Настройка кнопки отправки текстового сообщения.
     */
    private void createBtnSendText() {
        btnSend.setText(null);
        btnSend.setGraphic(new ImageView(BtnImages.SEND_MESSAGE_IMG));
        btnSend.setOnAction(e -> {
            lvCurrentDialog.sendText(); // Отправка текста при нажатии на кнопку
        });
    }

    /**
     * Настройка кнопки добавления изображений.
     */
    private void createBtnPictures() {
        btnAddPicture.setText("");
        btnAddPicture.setGraphic(new ImageView(BtnImages.BTN_ADD_CHAT_PIC_IMG));
        btnAddPicture.setTooltip(new Tooltip("Добавить изображение"));
        btnAddPicture.setOnAction(e -> {
            lvCurrentDialog.sendPicture(e); // Отправка изображения при нажатии на кнопку
        });
    }

    /**
     * Настройка кнопки добавления чертежей.
     */
    private void createBtnDrafts() {
        btnAddDraft.setText("");
        btnAddDraft.setGraphic(new ImageView(BtnImages.BTN_ADD_CHAT_DRAFT_IMG));
        btnAddDraft.setTooltip(new Tooltip("Добавить чертеж"));
        btnAddDraft.setOnAction(e -> {
            // Запуск задачи для открытия редактора чертежей
            Thread t = new Thread(new OpenDraftsEditorTask());
            t.setDaemon(true);
            t.start();
        });
    }

    /**
     * Настройка кнопки открытия списка комнат.
     */
    private void createBtnRooms() {
        btnRooms.setOnAction(e -> {
            chat.showChatGroups(); // Открытие списка комнат при нажатии на кнопку
        });
    }

    /**
     * Настройка текстового поля ввода сообщений.
     */
    private void createTextArea() {
        SplitPane.Divider divider = splitPane.getDividers().get(0);

        // Применение CSS стилей к текстовому полю
        taMessageText.setId("blobTextArea");

        // Настройка автоматического изменения высоты текстового поля
        textHolder.textProperty().bind(taMessageText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth() - 10);
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();

                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                divider.setPosition(1.0 - newHeight / splitPane.getHeight());
                taMessageText.resize(taMessageText.getWidth(), newHeight);
            }
        });

        taMessageText.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth() - 10);
            double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
            divider.setPosition(1.0 - newHeight / splitPane.getHeight());
            taMessageText.resize(taMessageText.getWidth(), newHeight);
        });

        // Обработка нажатия Ctrl+Enter для отправки сообщения
        taMessageText.setOnKeyPressed(e -> {
            if (taMessageText.isFocused() &&
                    e.isControlDown() &&
                    e.getCode().equals(KeyCode.ENTER))
                btnSend.fire();
        });
    }
}
