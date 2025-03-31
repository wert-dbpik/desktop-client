package ru.wert.tubus.chogori.chat.dialog.dialogController;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.ListViewManipulator;
import ru.wert.tubus.chogori.chat.roomsController.TabUsers;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;

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

    public static List<DialogListView> openRooms = new ArrayList<>();

    // Константы для управления размерами сообщений
    public static final float MESSAGE_WIDTH = 0.7f;
    public static final float PORTRAIT_WIDTH = 0.5f;
    public static final float LANDSCAPE_WIDTH = 0.7f;
    public static final float SQUARE_WIDTH = 0.6f;

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

        DialogListView openDialog = findDialogForRoom(room);

        // Если диалог еще не открыт, создаем новый
        if (openDialog == null) {
            dialogListView = new DialogListView(room, taMessageText);

            // Создаем Task для загрузки сообщений в фоновом режиме
            Task<List<Message>> loadMessagesTask = new Task<List<Message>>() {
                @Override
                protected List<Message> call() throws Exception {
                    // Загружаем сообщения для комнаты
                    List<Message> messages = CH_MESSAGES.findAllByRoom(room);

                    // Сортируем сообщения по дате creationTime (от старых к новым)
                    messages.sort(Comparator.comparing(Message::getCreationTime));

                    // Вставляем разделители дат
                    return new DateSeparators().insertDateSeparators(messages);
                }
            };

            // Обработка успешного завершения Task
            loadMessagesTask.setOnSucceeded(event -> {
                List<Message> messages = loadMessagesTask.getValue();

                dialogListView.getRoomMessages().setAll(messages == null ? new ArrayList<>() : messages); // Обновляем ObservableList

                // Настраиваем ListView для отображения сообщений
                dialogListView.setCellFactory((ListView<Message> tv) -> new DialogListCell(
                        room, dialogListView, this));
                dialogListView.setId("listViewWithMessages");

                // Добавляем манипулятор для обработки событий
                new ListViewManipulator(dialogListView, this);

                // Добавляем диалог в контейнер
                spDialogsContainer.getChildren().add(dialogListView);
                dialogListView.scrollToBottom();

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
        setRoomNameWithOnlineStatus(room);
        dialogListView.toFront();
        openRooms.add(dialogListView);
        log.info("Открыт диалог для комнаты: {}", room.getName());
    }

    /**
     * Устанавливает название комнаты с индикатором онлайн-статуса для one-to-one чатов
     * @param room Комната, для которой устанавливается название
     */
    /**
     * Устанавливает название комнаты с индикатором онлайн-статуса для one-to-one чатов
     * @param room Комната, для которой устанавливается название
     */
    private void setRoomNameWithOnlineStatus(Room room) {
        String roomName = ChatStaticMaster.getRoomName(room.getName());

        // Для one-to-one чатов
        if (room.getName().startsWith("one-to-one:")) {
            User interlocutor = ChatStaticMaster.getSecondUserInOneToOneChat(room);

            // Создаём динамическую привязку к онлайн-статусу
            BooleanBinding isOnlineBinding = Bindings.createBooleanBinding(
                    () -> chat.getRoomsController().getUsersOnline().stream()
                            .filter(u -> u.getUser().equals(interlocutor))
                            .findFirst()
                            .map(UserOnline::isOnline)
                            .orElse(false),
                    chat.getRoomsController().getUsersOnline() // Зависимость от изменений списка
            );

            // Обновляем интерфейс при изменении статуса
            isOnlineBinding.addListener((obs, oldValue, isOnline) -> {
                Platform.runLater(() -> {
                    if (isOnline) {
                        HBox roomNameBox = createOnlineStatusBox(roomName);
                        lblRoom.setGraphic(roomNameBox);
                        lblRoom.setText("");
                    } else {
                        lblRoom.setGraphic(null);
                        lblRoom.setText(roomName);
                    }
                });
            });

            // Первоначальная установка статуса
            boolean isOnlineNow = isOnlineBinding.get();
            if (isOnlineNow) {
                HBox roomNameBox = createOnlineStatusBox(roomName);
                lblRoom.setGraphic(roomNameBox);
                lblRoom.setText("");
                return;
            }
        }

        // Стандартное отображение
        lblRoom.setGraphic(null);
        lblRoom.setText(roomName);
    }

    /**
     * Создает HBox с индикатором онлайн-статуса и названием комнаты
     * @param roomName Название комнаты
     * @return HBox с элементами отображения
     */
    private HBox createOnlineStatusBox(String roomName) {
        HBox roomNameBox = new HBox();
        roomNameBox.setSpacing(5);
        roomNameBox.setAlignment(Pos.CENTER_LEFT);

        ImageView onlineDot = new ImageView(DOT_BLUE_IMG);
        onlineDot.setFitWidth(10);
        onlineDot.setFitHeight(10);

        roomNameBox.getChildren().addAll(onlineDot, new Label(roomName));
        return roomNameBox;
    }

    /**
     * Ищет открытый диалог для указанной комнаты.
     *
     * @param room Комната, для которой ищется диалог.
     * @return Найденный диалог или null, если диалог не найден.
     */
    public DialogListView findDialogForRoom(Room room) {
        for(DialogListView dialog : openRooms){
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
     * Возвращает текущую открытую комнату (если есть)
     * @return Текущая открытая комната или null, если ни одна комната не открыта
     */
    public Room getCurrentOpenRoom() {
        return dialogListView != null ? dialogListView.getRoom() : null;
    }

}