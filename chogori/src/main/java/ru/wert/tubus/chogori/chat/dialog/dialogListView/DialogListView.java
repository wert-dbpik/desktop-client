package ru.wert.tubus.chogori.chat.dialog.dialogListView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.chogori.images.ImageUtil;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.utils.MessageStatus;
import ru.wert.tubus.client.utils.MessageType;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static ru.wert.tubus.chogori.components.BtnChat.CHAT_OPEN;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Класс для отображения и управления списком сообщений в диалоге чата.
 * Обеспечивает отправку, получение и отображение сообщений различных типов.
 */
@Slf4j
public class DialogListView extends ListView<Message> {

    @Getter
    private final Room room; // Текущая комната чата
    private final TextArea taMessageText; // Текстовое поле для ввода сообщений
    @Getter
    private final ObservableList<Message> roomMessages = FXCollections.observableArrayList(); // Список сообщений
    private final SortedList<Message> sortedMessages; // Сортированная версия для ListView

    /**
     * Конструктор класса.
     * @param room Комната чата
     * @param taMessageText Текстовое поле для ввода сообщений
     */
    public DialogListView(Room room, TextArea taMessageText) {
        this.room = room;
        this.taMessageText = taMessageText;
        setId("dialogListView");

        this.sortedMessages = new SortedList<>(roomMessages,
                Comparator.comparing(Message::getCreationTime).thenComparing(Message::getTempId));

        // Обновляем сортировку при изменении данных
        sortedMessages.setComparator(Comparator.comparing(Message::getCreationTime)
                .thenComparing(m -> m.getTempId() != null ? m.getTempId() : ""));
        setItems(sortedMessages);
        log.info("Создан новый диалог для комнаты: {}", room.getName());
    }

    /**
     * Плавно добавляет сообщение в список с автоматической прокруткой
     * @param message Сообщение для добавления
     */
    public void addMessageSmoothly(Message message) {
        Platform.runLater(() -> {
            roomMessages.add(message);
            if (shouldScrollToNewMessage()) {
                smartScrollToLastMessage();
            }
            logAllMessagesOrdered();
        });
    }

    /**
     * Выводит в консоль все сообщения чата в порядке их отображения в ListView.
     * Формат вывода: "[ID] Текст"
     */
    public void logAllMessagesOrdered() {
        Platform.runLater(() -> {
            // Получаем отсортированный список сообщений
            List<Message> orderedMessages = new ArrayList<>(sortedMessages);

            for (Message message : orderedMessages) {
                log.info("Сообщение [{}] -  {}",
                        message.getId() != null ? message.getId() : message.getTempId(),
                        message.getText() != null ? message.getText() : "[нет текста]");
            }
        });
    }


    /**
     * Отправляет текстовое сообщение
     */
    public void sendText() {
        String text = taMessageText.getText();
        if (text == null || text.isEmpty()) return;

        Message message = createMessage(MessageType.CHAT_TEXT, text);
        sendMessage(message);
    }

    /**
     * Создает и отправляет сообщение с паспортами
     * @param str Строка с данными паспортов
     */
    public void createPassportsChatMessage(String str) {
        String text = parsePassportData(str);
        Message message = createMessage(MessageType.CHAT_PASSPORTS, text);
        sendMessage(message);
    }

    /**
     * Создает и отправляет сообщение с чертежами
     * @param str Строка с данными чертежей
     */
    public void createDraftsChatMessage(String str) {
        String text = parseDraftData(str);
        Message message = createMessage(MessageType.CHAT_DRAFTS, text);
        sendMessage(message);
    }

    /**
     * Создает и отправляет сообщение с комплектами чертежей
     * @param str Строка с данными комплектов
     */
    public void createFoldersChatMessage(String str) {
        String text = parseFolderData(str);
        Message message = createMessage(MessageType.CHAT_FOLDERS, text);
        sendMessage(message);
    }

    /**
     * Обрабатывает отправку изображений
     * @param event Событие нажатия кнопки
     */
    public void sendPicture(ActionEvent event) {
        List<File> files = chooseImageFiles(event);
        if (files != null && !files.isEmpty()) {
            createPicsChatMessage(files);
        }
    }

    /**
     * Создает и отправляет сообщение с изображениями
     * @param chosenFiles Список файлов изображений
     */
    public void createPicsChatMessage(List<File> chosenFiles) {
        String text = buildImageIdsString(chosenFiles);
        Message message = createMessage(MessageType.CHAT_PICS, text);
        sendMessage(message);
    }

    /**
     * Получает сообщение от сервера
     * @param message Полученное сообщение
     */
    public void receiveMessageFromServer(Message message) {
        if (isOwnMessage(message)) {
            handleOwnMessage(message);
        } else {
            handleIncomingMessage(message);
        }
    }

    /**
     * Умная прокрутка к последнему сообщению
     */
    public void smartScrollToLastMessage() {
        if (roomMessages.isEmpty()) return;

        int lastIndex = roomMessages.size() - 1;
        scrollTo(lastIndex);

        Platform.runLater(() -> {
            layout();
            scrollTo(lastIndex);
            new Timeline(new KeyFrame(Duration.millis(100), e -> scrollTo(lastIndex))).play();
        });
    }

    // Приватные вспомогательные методы

    private String generateTempId() {
        return "temp_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private Message createMessage(MessageType type, String text) {
        Message message = new Message();
        message.setTempId(generateTempId());
        message.setType(type);
        message.setRoomId(room.getId());
        message.setSenderId(CH_CURRENT_USER.getId());
        message.setCreationTime(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);
        message.setText(text);
        return message;
    }

    private void sendMessage(Message message) {
        SocketService.sendMessage(message);
        taMessageText.setText("");
        Platform.runLater(()->{
            addMessageSmoothly(message);
        });

    }

    private String parsePassportData(String str) {
        return parseData(str, "PP#");
    }

    private String parseDraftData(String str) {
        return parseData(str, "DR#");
    }

    private String parseFolderData(String str) {
        return parseData(str, "F#");
    }

    private String parseData(String str, String prefix) {
        StringBuilder text = new StringBuilder();
        Arrays.stream(str.replace("pik!", "").trim().split(" "))
                .filter(s -> s.startsWith(prefix.replace("#", "")))
                .forEach(s -> text.append(s.replace(prefix, "")).append(" "));
        return text.toString().trim();
    }

    private List<File> chooseImageFiles(ActionEvent event) {
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        return AppStatic.chooseManyFile(event, new File("C:\\"), filter);
    }

    private String buildImageIdsString(List<File> files) {
        StringBuilder text = new StringBuilder();
        files.forEach(file -> {
            Image image = new Image(file.toURI().toString());
            Pic pic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(pic.getId()).append(" ");
        });
        return text.toString().trim();
    }

    private boolean isOwnMessage(Message message) {
        return message.getSenderId() == CH_CURRENT_USER.getId();
    }

    private void handleOwnMessage(Message message) {
        if (CHAT_OPEN) ServiceMessaging.sendNotificationMessageDelivered(message);
        message.setStatus(MessageStatus.DELIVERED);
        updateOrAddMessage(message);

    }

    private void handleIncomingMessage(Message message) {
        Platform.runLater(() -> updateOrAddMessage(message));
    }

    public void updateOrAddMessage(Message message) {
        synchronized (roomMessages) {
            boolean updated = false;
            for (int i = 0; i < roomMessages.size(); i++) {
                Message m = roomMessages.get(i);
                if ((message.getTempId() != null && message.getTempId().equals(m.getTempId())) ||
                        (message.getId() != null && message.getId().equals(m.getId()))) {

                    // Удаляем старое сообщение из кэша
                    DialogListCell.clearCacheForMessage(m);

                    // Обновляем существующее сообщение
                    m.setId(message.getId());
                    m.setType(message.getType());
                    m.setText(message.getText());
                    m.setStatus(message.getStatus());
                    m.setCreationTime(message.getCreationTime());

                    updated = true;
                    break;
                }
            }

            if (!updated) {
                roomMessages.add(message);
            }

            // Обновляем отображение через основной список
            Platform.runLater(() -> {
                // Просто обновляем ListView, так как roomMessages уже изменен
                if (isListNearBottom()) {
                    smartScrollToLastMessage();
                }else {
                    // Сохраняем текущую позицию прокрутки
                    int firstVisibleIndex = getFirstVisibleIndex();
                    double scrollPosition = getVerticalScrollbar() != null ?
                            getVerticalScrollbar().getValue() : 0;

                    // Обновляем ListView
                    refresh();

                    // Восстанавливаем позицию
                    if (getVerticalScrollbar() != null) {
                        getVerticalScrollbar().setValue(scrollPosition);
                    }
                }
            });
        }
    }

    /**
     * Возвращает индекс первого видимого элемента
     */
    public int getFirstVisibleIndex() {
        ScrollBar vbar = getVerticalScrollbar();
        if (vbar == null) return 0;

        double position = vbar.getValue();
        return (int) (position * (roomMessages.size() - 1));
    }

    /**
     * Восстанавливает позицию прокрутки
     */
    public void restoreScrollPosition(int firstVisibleIndex) {
        if (roomMessages.isEmpty()) return;

        firstVisibleIndex = Math.min(Math.max(0, firstVisibleIndex), roomMessages.size() - 1);
        scrollTo(firstVisibleIndex);
    }

    /**
     * Проверяет, находится ли список около нижнего края
     */
    public boolean isListNearBottom() {
        ScrollBar vbar = getVerticalScrollbar();
        if (vbar == null) return true;
        return (vbar.getMax() - vbar.getValue()) < 0.3;
    }

    public ScrollBar getVerticalScrollbar() {
        return (ScrollBar) lookupAll(".scroll-bar").stream()
                .filter(n -> n instanceof ScrollBar &&
                        ((ScrollBar)n).getOrientation() == Orientation.VERTICAL)
                .findFirst()
                .orElse(null);
    }

    /**
     * Определяет нужно ли прокручивать список к новому сообщению
     */
    private boolean shouldScrollToNewMessage() {
        return isListNearBottom();
    }
}