package ru.wert.tubus.chogori.chat.dialog.dialogController;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.ListViewManipulator;
import ru.wert.tubus.chogori.chat.roomsController.TabUsers;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

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

    public static Map<DialogListView, Boolean> openRooms = new HashMap<>();

    /**
     *
     * @param room
     */
    public static void openOneRoom(DialogListView room) {
        closeAllRooms();
        // 2. Установить true только для указанной комнаты
        openRooms.put(room, true);
    }

    public static void closeAllRooms() {
        // 1. Сбросить все значения в false
        for (Map.Entry<DialogListView, Boolean> entry : openRooms.entrySet()) {
            entry.setValue(false);
        }
    }

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

                // Слушатель + задержка для подстраховки
                AtomicBoolean scrolled = new AtomicBoolean(false);
                dialogListView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
                    if (!scrolled.get() && newVal.getWidth() > 0 && newVal.getHeight() > 0) {
                        scrolled.set(true);
                        Platform.runLater(() -> dialogListView.smartScrollToLastMessage());
                    }
                });

                // Подстраховка на случай, если слушатель не сработает
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        Platform.runLater(() -> {
                            if (!scrolled.get()) {
                                dialogListView.smartScrollToLastMessage();
                                scrolled.set(true);
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

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

        openRooms.put(dialogListView, false);
        //Делаем комнату активной
        openOneRoom(dialogListView);

        log.info("Открыт диалог для комнаты: {}", room.getName());

        // После открытия диалога помечаем сообщения как доставленные
        markMessagesAsDelivered(room);
    }

    /**
     * Помечает сообщения как доставленные и отправляет уведомления на сервер
     * @param room Комната, для которой нужно пометить сообщения как доставленные
     */
    public void markMessagesAsDelivered(Room room) {
        Task<Void> markDeliveredTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Получаем собеседника (для one-to-one чатов)
                User interlocutor = ChatStaticMaster.getSecondUserInOneToOneChat(room);
                if (interlocutor == null) {
                    log.warn("Не удалось определить собеседника для комнаты {}", room.getName());
                    return null;
                }

                // Получаем все сообщения в комнате, которые еще не были доставлены
                List<Message> undeliveredMessages = ChogoriServices.CH_MESSAGES
                        .findUndeliveredMessages(room.getId(), CH_CURRENT_USER.getId());

                // Для каждого сообщения отправляем уведомление о доставке
                for (Message message : undeliveredMessages) {
                    try {
                        // Отправляем уведомление о доставке через сервис сообщений
                        ServiceMessaging.sendNotificationMessageDelivered(message);

                        // Помечаем сообщение как доставленное локально
                        message.setStatus(Message.MessageStatus.DELIVERED);
                        ChogoriServices.CH_MESSAGES.update(message);

                        log.debug("Отправлено уведомление о доставке для сообщения {}", message.getId());
                    } catch (Exception e) {
                        log.error("Ошибка при отправке уведомления для сообщения {}: {}",
                                message.getId(), e.getMessage());
                    }
                }

                return null;
            }
        };

        new Thread(markDeliveredTask).start();
    }

    /**
     * Устанавливает название комнаты с индикатором онлайн-статуса для one-to-one чатов
     * @param room Комната, для которой устанавливается название
     */
    /**
     * Устанавливает название комнаты с индикатором онлайн-статуса для one-to-one чатов
     * @param room Комната, для которой устанавливается название
     */
    public void setRoomNameWithOnlineStatus(Room room) {
        String roomName = ChatStaticMaster.getRoomName(room.getName());

        // Для one-to-one чатов
        if (room.getName().startsWith("one-to-one:")) {
            User interlocutor = ChatStaticMaster.getSecondUserInOneToOneChat(room);
            if (interlocutor == null) {
                lblRoom.setGraphic(null);
                lblRoom.setText(roomName);
                return;
            }

            // Создаём динамическую привязку к онлайн-статусу
            BooleanBinding isOnlineBinding = Bindings.createBooleanBinding(
                    () -> chat.getRoomsController().getUsersOnline().stream()
                            .anyMatch(u -> u.getUser().equals(interlocutor) &&
                                    u.isOnline()),
                    chat.getRoomsController().getUsersOnline() // Зависимость от изменений списка
            );

            // Обновляем интерфейс при изменении статуса
            isOnlineBinding.addListener((obs, oldValue, isOnline) -> {
                Platform.runLater(() -> updateRoomNameLabel(roomName, isOnline));
            });

            // Первоначальная установка статуса
            updateRoomNameLabel(roomName, isOnlineBinding.get());
        } else {
            // Стандартное отображение
            lblRoom.setGraphic(null);
            lblRoom.setText(roomName);
        }
    }

    /**
     * Обновляет название комнаты с индикатором онлайн-статуса в виде подписи "online".
     * @param roomName Название комнаты
     * @param isOnline Флаг онлайн-статуса
     */
    private void updateRoomNameLabel(String roomName, boolean isOnline) {
        if (isOnline) {
            VBox roomNameBox = createOnlineStatusBox(roomName);
            lblRoom.setGraphic(roomNameBox);
            lblRoom.setText("");
        } else {
            lblRoom.setGraphic(null);
            lblRoom.setText(roomName);
            lblRoom.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

    /**
     * Создает VBox с названием комнаты и подписью "online" синего цвета
     * @param roomName Название комнаты
     * @return VBox с элементами отображения
     */
    private VBox createOnlineStatusBox(String roomName) {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER_LEFT);  // Выравнивание содержимого по левому краю
        container.setSpacing(1);  // Уменьшенный зазор между элементами
        container.setPadding(new Insets(0, 0, 0, 5)); // Увеличенный отступ слева для всего контейнера

        // Основное название комнаты
        Label nameLabel = new Label(roomName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setAlignment(Pos.CENTER_LEFT);  // Выравнивание текста по левому краю

        // Подпись "online"
        Label onlineLabel = new Label("online");
        onlineLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: #8ebee7;");
        onlineLabel.setAlignment(Pos.CENTER_LEFT);  // Выравнивание текста по левому краю
        onlineLabel.setPadding(new Insets(-2, 0, 0, 2));  // Небольшой отступ слева и подъем вверх

        container.getChildren().addAll(nameLabel, onlineLabel);
        return container;
    }

    /**
     * Ищет открытый диалог для указанной комнаты.
     *
     * @param room Комната, для которой ищется диалог.
     * @return Найденный диалог или null, если диалог не найден.
     */
    public DialogListView findDialogForRoom(Room room) {
        for(DialogListView dialog : openRooms.keySet()){
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