package ru.wert.tubus.chogori.chat.dialog.dialogListView;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.chogori.images.ImageUtil;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Room;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.wert.tubus.chogori.components.BtnChat.CHAT_OPEN;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Класс DialogListView отвечает за отображение списка сообщений в диалоге чата.
 * Он также обрабатывает отправку и получение сообщений, включая текстовые сообщения,
 * изображения, чертежи и паспорта.
 */
@Slf4j
public class DialogListView extends ListView<Message> {

    @Getter
    private final Room room; // Текущая комната чата
    private final TextArea taMessageText; // Текстовое поле для ввода сообщений
    @Getter
    private final ObservableList<Message> roomMessages = FXCollections.observableArrayList(); // Список сообщений

    /**
     * Конструктор класса.
     *
     * @param room         Комната, для которой создается диалог.
     * @param taMessageText Текстовое поле для ввода сообщений.
     */
    public DialogListView(Room room, TextArea taMessageText) {
        this.room = room;
        this.taMessageText = taMessageText;
        setId("dialogListView");

        setItems(roomMessages); // Привязываем ObservableList к ListView

        log.info("Создан новый диалог для комнаты: {}", room.getName());
    }

    /**
     * Создает временный ID сообщения
     */
    private String generateTempId(){
        return "temp_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * Создает и отправляет сообщение с паспортами.
     *
     * @param str Строка с данными паспортов.
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
        sendMessageToRecipient(message);
        log.debug("Создано сообщение с паспортами: {}", text.toString().trim());
    }

    /**
     * Создает и отправляет сообщение с чертежами.
     *
     * @param str Строка с данными чертежей.
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
        sendMessageToRecipient(message);
        log.debug("Создано сообщение с чертежами: {}", text.toString().trim());
    }

    /**
     * Создает и отправляет сообщение с комплектами чертежей.
     *
     * @param str Строка с данными комплектов чертежей.
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
        sendMessageToRecipient(message);
        log.debug("Создано сообщение с комплектами чертежей: {}", text.toString().trim());
    }

    /**
     * Обрабатывает отправку изображений.
     *
     * @param event Событие нажатия на кнопку.
     */
    public void sendPicture(ActionEvent event) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if (chosenFiles == null || chosenFiles.isEmpty()) return;

        createPicsChatMessage(chosenFiles);
        log.debug("Пользователь выбрал изображения для отправки: {}", chosenFiles.size());
    }

    /**
     * Создает и отправляет сообщение с изображениями.
     *
     * @param chosenFiles Список выбранных файлов изображений.
     */
    public void createPicsChatMessage(List<File> chosenFiles) {
        StringBuilder text = new StringBuilder();
        for (File file : chosenFiles) {
            Image image = new Image(file.toURI().toString());
            Pic savedPic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(savedPic.getId());
            text.append(" ");
        }

        Message message = createChatMessage(Message.MessageType.CHAT_PICS, text.toString().trim());
        taMessageText.setText("");
        sendMessageToRecipient(message);
        log.debug("Создано сообщение с изображениями: {}", text.toString().trim());
    }

    /**
     * Отправляет текстовое сообщение.
     */
    public void sendText() {
        String text = taMessageText.getText();
        if (text == null || text.isEmpty()) return;

        // Создаем сообщение
        Message message = new Message();
        message.setTempId(generateTempId());
        message.setType(Message.MessageType.CHAT_TEXT);
        message.setRoomId(room.getId());
        message.setSenderId(CH_CURRENT_USER.getId());
        message.setCreationTime(LocalDateTime.now());
        message.setStatus(Message.MessageStatus.SENT);
        message.setText(text);

        // Отправляем сообщение через SocketService
        SocketService.sendMessage(message);
        roomMessages.add(message); // Добавляем сообщение в список

        smartScrollToLastMessage();

        taMessageText.setText(""); // Очищаем текстовое поле
    }

    /**
     * Создает объект сообщения.
     *
     * @param type Тип сообщения.
     * @param text Текст сообщения.
     * @return Созданное сообщение.
     */
    private Message createChatMessage(Message.MessageType type, String text) {
        Message message = new Message();
        message.setTempId(generateTempId());
        message.setType(type);
        message.setRoomId(room.getId());
        message.setSenderId(CH_CURRENT_USER.getId());
        message.setCreationTime(LocalDateTime.now());
        message.setStatus(Message.MessageStatus.SENT);
        message.setText(text);
        return message;
    }

    /**
     * Отправляет сообщение получателю.
     *
     * @param message Сообщение для отправки.
     */
    private void sendMessageToRecipient(Message message) {
        SocketService.sendMessage(message);
        Platform.runLater(() -> {
            roomMessages.add(message); // Добавляем сообщение в ObservableList
            smartScrollToLastMessage();
            log.debug("Сообщение отправлено и добавлено в список: {}", message.getText());
        });
    }

    /**
     * Получает сообщение от сервера.
     *
     * @param message Полученное сообщение.
     */
    public void receiveMessageFromServer(Message message) {
        if (message.getSenderId().equals(CH_CURRENT_USER.getId())) {
            // Это наше же сообщение, вернувшееся от сервера
            if (CHAT_OPEN) ServiceMessaging.sendNotificationMessageDelivered(message);

            // Обновляем статус сообщения
            message.setStatus(Message.MessageStatus.DELIVERED);

            Platform.runLater(() -> {
                // Ищем сообщение с таким же tempId в списке
                boolean messageExists = roomMessages.stream()
                        .anyMatch(m -> message.getTempId() != null &&
                                message.getTempId().equals(m.getTempId()));

                if (!messageExists) {
                    roomMessages.add(message);
                    if (isListNearBottom()) smartScrollToLastMessage();
                    log.debug("Подтверждение отправки сообщения: {}", message.getText());
                }
            });
        } else {
            // Это входящее сообщение от другого пользователя
            Platform.runLater(() -> {
                roomMessages.add(message);
                if (isListNearBottom()) smartScrollToLastMessage();
                log.debug("Получено новое сообщение: {}", message.getText());
            });
        }
    }

    public void smartScrollToLastMessage() {
        if (getItems().isEmpty()) return;
        int lastIndex = getItems().size() - 1;

        scrollTo(lastIndex);
        Platform.runLater(() -> {
            layout();
            scrollTo(lastIndex);
        });
    }

    public boolean isListNearBottom() {
        ScrollBar vbar = getVerticalScrollbar();
        if (vbar == null) return true;

        double position = vbar.getValue();
        double max = vbar.getMax();
        return (max - position) < 0.3;
    }

    private ScrollBar getVerticalScrollbar() {
        for (Node node : lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL) {
                return (ScrollBar) node;
            }
        }
        return null;
    }

}
