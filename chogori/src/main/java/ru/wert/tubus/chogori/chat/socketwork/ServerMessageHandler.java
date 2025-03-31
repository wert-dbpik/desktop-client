package ru.wert.tubus.chogori.chat.socketwork;

import com.google.gson.Gson;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.chogori.chat.socketwork.messageHandlers.*;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.deleteMessageFromOpenRooms;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.updateMessageInOpenRooms;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_SHOW_NOTIFICATION_LINE;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_NOTIFICATION;

/**
 * Основной класс-менеджер для обработки сообщений от сервера.
 * Делегирует обработку специализированным классам в зависимости от типа сообщения.
 */
@Slf4j
public class ServerMessageHandler {

    public static RoomsController roomsController;
    public static DialogController dialogController;

    /**
     * Обрабатывает входящее сообщение от сервера.
     * @param message Входящее сообщение.
     */
    public static void handle(Message message) {
        if (message == null) return;

        log.info("Message from server received: {}", message.toUsefulString());

        // Обработка уведомлений в строке состояния
        if (SP_NOTIFICATION != null && CH_SHOW_NOTIFICATION_LINE) {
            Platform.runLater(() -> {
                Message.MessageType type = message.getType();
                boolean isAdmin = CH_CURRENT_USER.getUserGroup().isAdministrate();
                boolean isUserInOut = type.equals(Message.MessageType.USER_IN) ||
                        type.equals(Message.MessageType.USER_OUT);

                // Сообщения о входе/выходе показываем только админам
                if (!isUserInOut || isAdmin) {
                    SP_NOTIFICATION.setText(processMessage(message));
                }
            });
        }

        // Обработка сообщений чата
        processChatMessage(message);
    }

    /**
     * Обрабатывает сообщения чата с учетом активности комнаты
     * @param message Входящее сообщение
     */
    private static void processChatMessage(Message message) {
        Message.MessageType type = message.getType();
        log.debug("Обработка сообщения типа: {}", type);

        // Обрабатываем только сообщения типа PUSH
        if (type == Message.MessageType.PUSH) {
            try {
                // Парсим вложенное сообщение из текста PUSH-уведомления
                Gson gson = GsonConfiguration.createGson();
                Message innerMessage = gson.fromJson(message.getText(), Message.class);

                // Проверяем, что вложенное сообщение является CHAT_ типом
                if (innerMessage.getType().name().startsWith("CHAT_")) {
                    Platform.runLater(() -> {
                        // Проверяем, открыта ли комната этого сообщения
                        boolean isRoomOpen = false;
                        Room currentRoom = dialogController != null ? dialogController.getCurrentOpenRoom() : null;
                        log.debug("Текущая открытая комната: {}", currentRoom != null ? currentRoom.getId() : "null");
                        log.debug("Комната сообщения: {}", innerMessage.getRoomId());

                        if (currentRoom != null && currentRoom.getId().equals(innerMessage.getRoomId())) {
                            isRoomOpen = true;
                        }

                        // Если комната не открыта - показываем push-уведомление
                        log.debug("Комната открыта? {}", isRoomOpen);
                        if (!isRoomOpen) {
                            log.debug("Показываем пуш для сообщения из закрытой комнаты");
                            PushNotification.show(innerMessage); // Передаем вложенное сообщение
                        }

                        // Передаем сообщение в соответствующий диалог
                        for (DialogListView dialog : DialogController.openRooms) {
                            if (dialog.getRoom().getId().equals(innerMessage.getRoomId())) {
                                dialog.receiveMessageFromServer(innerMessage);
                                break;
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Ошибка при парсинге вложенного сообщения PUSH: {}", e.getMessage());
            }
        }
    }

    /**
     * Определяет тип сообщения и делегирует обработку соответствующему обработчику.
     * @param message Входящее сообщение.
     * @return Строка для отображения в уведомлении.
     */
    private static String processMessage(Message message) {
        Message.MessageType type = message.getType();
        StringBuilder str = new StringBuilder();

        switch (type) {
            case USER_IN:
            case USER_OUT:
                UserStatusHandler.handle(message, type, str, roomsController);
                break;

            case CHAT_TEXT:
            case CHAT_PASSPORTS:
            case CHAT_DRAFTS:
            case CHAT_FOLDERS:
            case CHAT_PICS:
                ChatMessageHandler.handle(message);
                break;

            case UPDATE_MESSAGE:
                updateMessageInOpenRooms(message);
                break;

            case DELETE_MESSAGE:
                deleteMessageFromOpenRooms(message);
                break;

            case ADD_DRAFT:
            case UPDATE_DRAFT:
            case DELETE_DRAFT:
                DraftMessageHandler.handle(message, type, str);
                break;

            case ADD_FOLDER:
            case UPDATE_FOLDER:
            case DELETE_FOLDER:
                FolderMessageHandler.handle(message, type, str);
                break;

            case ADD_PRODUCT:
            case UPDATE_PRODUCT:
            case DELETE_PRODUCT:
                ProductMessageHandler.handle(message, type, str);
                break;

            case ADD_PRODUCT_GROUP:
            case UPDATE_PRODUCT_GROUP:
            case DELETE_PRODUCT_GROUP:
                OtherChangesHandler.handle();
                break;

            case PUSH:
                PushMessageHandler.handle(message);
                break;
        }

        return str.toString();
    }
}
