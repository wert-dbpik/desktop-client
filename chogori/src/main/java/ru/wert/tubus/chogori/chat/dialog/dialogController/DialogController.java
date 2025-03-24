package ru.wert.tubus.chogori.chat.dialog.dialogController;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.ListViewManipulator;
import ru.wert.tubus.chogori.chat.util.ChatMaster;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.serviceREST.MessageService;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;

/**
 * Контроллер для управления диалогами в комнатах чата.
 * Отвечает за отображение сообщений, отправку текста, изображений и чертежей.
 */
@Slf4j
public class DialogController {

    @FXML
    @Getter
    private StackPane spMessageContainer; // Контейнер для текстового поля ввода сообщения (taMessageText)

    @FXML
    @Getter
    private SplitPane splitPane; // Разделитель для управления размерами элементов интерфейса

    @FXML
    @Getter
    private Button btnBackToRooms; // Кнопка для открытия списка доступных комнат <

    @FXML
    @Getter
    private Label lblRoom; // Название текущей комнаты

    @FXML
    @Getter
    private TextArea taMessageText; // Текстовое поле для ввода сообщений

    @FXML
    @Getter
    private StackPane spDialogsContainer; // Контейнер для отображения сообщений в текущей комнате

    @FXML
    @Getter
    private Button btnSend; // Кнопка для отправки текстового сообщения

    @FXML
    @Getter
    private Button btnAddPicture; // Кнопка для добавления изображения

    @FXML
    @Getter
    private Button btnAddDraft; // Кнопка для добавления чертежа

    @Getter
    private SideChat chat; // Ссылка на основной класс чата

    List<DialogListView> openDialogs = new ArrayList<>();

    // Константы для управления размерами сообщений
    public static final float MESSAGE_WIDTH = 0.7f;
    public static final float PORTRAIT_WIDTH = 0.5f;
    public static final float LANDSCAPE_WIDTH = 0.7f;
    public static final float SQUARE_WIDTH = 0.6f;

    private Room room; // Текущая комната
    @Getter private DialogListView dialogListView; // Текущий диалог (список сообщений)

    /**
     * Инициализация контроллера.
     *
     * @param chat Ссылка на основной класс чата.
     */
    public void init(SideChat chat) {
        this.chat = chat;
        ServerMessageHandler.dialogController = this;
        log.info("Инициализация DialogController для чата");
    }

    /**
     * Открывает диалог для указанной комнаты.
     * Если диалог уже открыт, переключается на него, иначе создает новый.
     *
     * @param room Комната, для которой нужно открыть диалог.
     */
    public void openDialogForRoom(Room room) {
        this.room = room;
        DialogListView openDialog = findDialogForRoom(room);

        // Если диалог еще не открыт, создаем новый
        if (openDialog == null) {
            dialogListView = new DialogListView(room, taMessageText);

            // Создаем Task для загрузки сообщений в фоновом режиме
            Task<List<Message>> loadMessagesTask = new Task<List<Message>>() {
                @Override
                protected List<Message> call() throws Exception {
                    // Загружаем сообщения для комнаты
                    return new DateSeparators().insertDateSeparators(CH_MESSAGES.findAllByRoom(room));
                }
            };

            // Обработка успешного завершения Task
            loadMessagesTask.setOnSucceeded(event -> {
                List<Message> messages = loadMessagesTask.getValue();

                markMessagesAsDelivered(messages);

                dialogListView.getRoomMessages().setAll(messages == null ? new ArrayList<>() : messages); // Обновляем ObservableList


                // Настраиваем ListView для отображения сообщений
                dialogListView.setCellFactory((ListView<Message> tv) -> new DialogListCell(room, dialogListView));
                dialogListView.setId("listViewWithMessages");

                // Добавляем манипулятор для обработки событий
                new ListViewManipulator(dialogListView, this);

                // Добавляем диалог в контейнер
                spDialogsContainer.getChildren().add(dialogListView);
                scrollToBottom();

                log.info("Сообщения успешно загружены для комнаты: {}", room.getName());
            });

            // Обработка ошибок в Task
            loadMessagesTask.setOnFailed(event -> {
                Throwable exception = loadMessagesTask.getException();
                log.error("Ошибка при загрузке сообщений: {}", exception.getMessage(), exception);
            });

            // Запускаем Task в отдельном потоке
            new Thread(loadMessagesTask).start();
        } else {
            dialogListView = openDialog;
        }

        // Устанавливаем название комнаты и переключаемся на диалог
        lblRoom.setText(ChatMaster.getRoomName(room.getName()));
        dialogListView.toFront();
        openDialogs.add(dialogListView);
        log.info("Открыт диалог для комнаты: {}", room.getName());
    }



    /**
     * Ищет открытый диалог для указанной комнаты.
     *
     * @param room Комната, для которой ищется диалог.
     * @return Найденный диалог или null, если диалог не найден.
     */
    public DialogListView findDialogForRoom(Room room) {
        for(DialogListView dialog : openDialogs){
            if(dialog.getRoom().equals(room))
                return dialog;
        }
        return null;
    }

    /**
     * Инициализация контроллера при загрузке FXML.
     */
    @FXML
    void initialize() {

        ButtonsManager buttonsManager = new ButtonsManager(this);
        buttonsManager.createBtnSendText(); // Настройка кнопки отправки текста
        buttonsManager.createBtnPictures(); // Настройка кнопки добавления изображений
        buttonsManager.createBtnDrafts();   // Настройка кнопки добавления чертежей
        buttonsManager.createBtnRooms();    // Настройка кнопки открытия списка комнат

        InputTextArea inputTextArea = new InputTextArea(this);
        inputTextArea.createTextArea();    // Настройка текстового поля ввода
    }

    /**
     * Прокручивает вертикальный ScrollBar контейнера spDialogsContainer в самый низ.
     */
    public void scrollToBottom() {
        Platform.runLater(() -> dialogListView.scrollTo(dialogListView.getItems().size() - 1));
        log.debug("Прокрутка списка сообщений вниз");
    }

    public static void markMessagesAsDelivered(List<Message> messages){
        for(Message m: messages){
            if(m.getStatus().equals(Message.MessageStatus.RECEIVED)){
                m.setStatus(Message.MessageStatus.DELIVERED);
                MessageService.getInstance().update(m);
            }

        }
    }
}