package ru.wert.tubus.chogori.chat.dialog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.OpenDraftsEditorTask;
import ru.wert.tubus.chogori.chat.util.ChatMaster;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.images.BtnImages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;

/**
 * Контроллер для управления диалогами в комнатах чата.
 * Отвечает за отображение сообщений, отправку текста, изображений и чертежей.
 */
@Slf4j
public class DialogController {

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
    @Getter
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

    private ObservableList<Message> roomMessages = FXCollections.observableArrayList(); // Список сообщений в текущей комнате
    private Room room; // Текущая комната
    private ListViewDialog lvCurrentDialog; // Текущий диалог (список сообщений)

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
        ListViewDialog foundDialog = findDialogForRoom(room);

        // Если диалог еще не открыт, создаем новый
        if (foundDialog == null) {
            lvCurrentDialog = new ListViewDialog(room, taMessageText);

            // Создаем Task для загрузки сообщений в фоновом режиме
            Task<List<Message>> loadMessagesTask = new Task<List<Message>>() {
                @Override
                protected List<Message> call() throws Exception {
                    // Загружаем сообщения для комнаты
                    return insertDateSeparators(CH_MESSAGES.findAllByRoom(room));
                }
            };

            // Обработка успешного завершения Task
            loadMessagesTask.setOnSucceeded(event -> {
                List<Message> messages = loadMessagesTask.getValue();
                roomMessages.setAll(messages == null ? new ArrayList<>() : messages); // Обновляем ObservableList

                // Настраиваем ListView для отображения сообщений
                lvCurrentDialog.setItems(roomMessages); // Привязываем roomMessages к ListView
                lvCurrentDialog.setCellFactory((ListView<Message> tv) -> new ChatListCell(room));
                lvCurrentDialog.setId("listViewWithMessages");

                // Добавляем манипулятор для обработки событий
                new ListViewWithMessages_Manipulator(lvCurrentDialog, this);

                // Добавляем диалог в контейнер
                spDialogsContainer.getChildren().add(lvCurrentDialog);
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
            lvCurrentDialog = foundDialog;
        }

        // Устанавливаем название комнаты и переключаемся на диалог
        lblRoom.setText(ChatMaster.getRoomName(room.getName()));
        lvCurrentDialog.toFront();
        log.info("Открыт диалог для комнаты: {}", room.getName());
    }

    /**
     * Перебирает список сообщений и вставляет сепараторы при смене даты в сообщениях.
     * Если сегодняшняя дата больше, чем дата последнего сообщения, добавляет сепаратор с текущей датой в конец списка.
     * Если список пуст, добавляет только сепаратор с текущей датой.
     *
     * @param messages исходная коллекция сообщений
     * @return список сообщений с добавленными сепараторами
     */
    private List<Message> insertDateSeparators(List<Message> messages) {
        // Если список сообщений пуст, добавляем только сепаратор с текущей датой
        if (messages == null || messages.isEmpty()) {
            List<Message> result = new ArrayList<>();
            result.add(getSeparatorMessage(LocalDateTime.now())); // Добавляем сепаратор с текущей датой
            log.debug("Добавлен сепаратор с текущей датой, так как список сообщений пуст");
            return result;
        }

        // Сортируем сообщения по времени создания
        messages.sort(Comparator.comparing(Message::getCreationTime));

        // Создаем временный список для хранения сообщений с разделителями
        List<Message> messagesWithSeparators = new ArrayList<>();

        // Переменная для хранения последней даты
        LocalDateTime lastDate = null;
        for (Message message : messages) {
            LocalDateTime currentDate = message.getCreationTime().toLocalDate().atStartOfDay();

            // Если дата сообщения отличается от последней даты, добавляем разделитель
            if (lastDate == null || !currentDate.isEqual(lastDate)) {
                // Создаем разделитель с датой
                Message separator = getSeparatorMessage(currentDate);

                // Добавляем разделитель в список
                messagesWithSeparators.add(separator);
                lastDate = currentDate; // Обновляем последнюю дату
                log.debug("Добавлен сепаратор с датой: {}", lastDate);
            }

            // Добавляем текущее сообщение в список
            messagesWithSeparators.add(message);
        }

        // Получаем текущую дату
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        // Получаем дату последнего сообщения
        LocalDateTime lastMessageDate = messages.get(messages.size() - 1).getCreationTime().toLocalDate().atStartOfDay();

        // Если сегодняшняя дата больше, чем дата последнего сообщения, добавляем сепаратор с текущей датой
        if (today.isAfter(lastMessageDate)) {
            Message separator = getSeparatorMessage(LocalDateTime.now());
            messagesWithSeparators.add(separator);
            log.debug("Добавлен сепаратор с текущей датой: {}", today);
        }

        return messagesWithSeparators;
    }

    /**
     * Создает сообщение-сепаратор с указанной датой.
     * Дата форматируется в формате "dd.MM.yyyy".
     *
     * @param dateTime дата и время для сепаратора
     * @return сообщение-сепаратор
     */
    private Message getSeparatorMessage(LocalDateTime dateTime) {
        Message separator = new Message();
        separator.setType(Message.MessageType.CHAT_SEPARATOR);
        separator.setCreationTime(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = dateTime.format(formatter);

        separator.setText(formattedDate); // Устанавливаем текст сепаратора как строку с датой
        return separator;
    }

    /**
     * Ищет открытый диалог для указанной комнаты.
     *
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
            log.debug("Отправлено текстовое сообщение");
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
            log.debug("Отправлено изображение");
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
            log.debug("Запущена задача для открытия редактора чертежей");
        });
    }

    /**
     * Настройка кнопки открытия списка комнат.
     */
    private void createBtnRooms() {
        btnRooms.setOnAction(e -> {
            chat.showChatGroups(); // Открытие списка комнат при нажатии на кнопку
            log.debug("Открыт список комнат");
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

    /**
     * Прокручивает вертикальный ScrollBar контейнера spDialogsContainer в самый низ.
     */
    public void scrollToBottom() {
        Platform.runLater(() -> lvCurrentDialog.scrollTo(lvCurrentDialog.getItems().size() - 1));
        log.debug("Прокрутка списка сообщений вниз");
    }
}