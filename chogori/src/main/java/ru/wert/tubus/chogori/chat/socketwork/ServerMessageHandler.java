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
import ru.wert.tubus.client.entity.serviceREST.RoomService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.util.Optional;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
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

        // Все сообщения должны проходить через обработку
        String notificationText = processMessage(message);

        // Отображение уведомления (если нужно)
        if (notificationText != null && !notificationText.isEmpty() &&
                SP_NOTIFICATION != null && CH_SHOW_NOTIFICATION_LINE) {

            Platform.runLater(() -> {
                Message.MessageType type = message.getType();
                boolean isAdmin = CH_CURRENT_USER.getUserGroup().isAdministrate();
                boolean isUserInOut = type.equals(Message.MessageType.USER_IN) ||
                        type.equals(Message.MessageType.USER_OUT);

                // Сообщения о входе/выходе показываем только админам
                if (!isUserInOut || isAdmin) {
                    SP_NOTIFICATION.setText(notificationText);
                }
            });
        }

        if(message.getType().equals(Message.MessageType.CHAT_UPDATE_TEMP_ID))
            handleTempIdUpdate(message);
        else
            // Обработка сообщений чата
            processChatMessage(message);
    }

    /**
     * Обрабатывает сообщения чата с учетом активности комнаты и состояния приложения
     * @param message Входящее сообщение
     */
    private static void processChatMessage(Message message) {
        Message.MessageType type = message.getType();
        log.debug("Обработка сообщения типа: {}", type);

        // Обрабатываем сообщения типа PUSH и CHAT_
        if (type == Message.MessageType.PUSH || type.name().startsWith("CHAT_")) {
            try {
                Message messageToProcess;

                // Для PUSH-сообщений парсим вложенное сообщение из текста
                if (type == Message.MessageType.PUSH) {
                    Gson gson = GsonConfiguration.createGson();
                    messageToProcess = gson.fromJson(message.getText(), Message.class);
                } else {
                    // Для CHAT_ сообщений используем само сообщение
                    messageToProcess = message;
                }

                // Проверяем, что сообщение является CHAT_ типом
                if (messageToProcess.getType().name().startsWith("CHAT_")) {
                    Platform.runLater(() -> {
                        // Проверяем, открыта ли комната этого сообщения
                        boolean isRoomOpen = false;
                        Room currentRoom = dialogController != null ? dialogController.getCurrentOpenRoom() : null;
                        log.debug("Текущая открытая комната: {}", currentRoom != null ? currentRoom.getId() : "null");
                        log.debug("Комната сообщения: {}", messageToProcess.getRoomId());

                        if (currentRoom != null && currentRoom.getId().equals(messageToProcess.getRoomId())) {
                            isRoomOpen = true;
                        }

                        // Проверяем, свернуто ли приложение
                        boolean isAppMinimized = WinformStatic.WF_MAIN_STAGE != null
                                && WinformStatic.WF_MAIN_STAGE.isIconified();

                        // Показываем уведомление если:
                        // 1. Комната не открыта ИЛИ
                        // 2. Приложение свернуто
                        boolean showNotification = !isRoomOpen || isAppMinimized;
                        log.debug("Комната открыта? {}, Приложение свернуто? {}", isRoomOpen, isAppMinimized);

                        if (showNotification) {
                            log.debug("Показываем пуш для сообщения");
                            PushNotification.show(messageToProcess);
                        }

                        // Передаем сообщение в соответствующий диалог
                        for (DialogListView dialog : DialogController.openRooms) {
                            if (dialog.getRoom().getId().equals(messageToProcess.getRoomId())) {
                                dialog.receiveMessageFromServer(messageToProcess);
                                break;
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Ошибка при обработке сообщения чата: {}", e.getMessage());
            }
        }
    }

    /**
     * Обрабатывает сообщение с обновлением временного ID
     * @param message Сообщение с новым ID (в поле tempId хранится оригинальный временный ID)
     */
    private static void handleTempIdUpdate(Message message) {
        Room room = RoomService.getInstance().findById(message.getRoomId());
        if (room == null) {
            log.warn("Комната для обновления ID не найдена");
            return;
        }

        DialogListView dialog = ServerMessageHandler.dialogController.findDialogForRoom(room);
        if (dialog == null) {
            log.warn("Диалог для комнаты {} не найден", room.getId());
            return;
        }

        // Ищем сообщение с совпадающим tempId
        Optional<Message> existingMessage = dialog.getItems().stream()
                .filter(m -> m.getTempId() != null && m.getTempId().equals(message.getTempId()))
                .findFirst();

        if (existingMessage.isPresent()) {
            // Обновляем ID сообщения
            Message msgToUpdate = existingMessage.get();
            msgToUpdate.setId(message.getId());

            // Обновляем статус, если нужно
            msgToUpdate.setStatus(Message.MessageStatus.DELIVERED);

            // Обновляем в UI
            Platform.runLater(()->{
                dialog.refresh();
                dialog.scrollTo(msgToUpdate);
            });


            // Обновляем в базе
            CH_MESSAGES.update(msgToUpdate);

            log.debug("Обновлен ID сообщения. TempId: {}, новый ID: {}",
                    message.getTempId(), message.getId());
        } else {
            log.warn("Сообщение с tempId {} не найдено для обновления", message.getTempId());
        }
    }

    /**
     * Определяет тип сообщения и делегирует обработку соответствующему обработчику.
     * @param message Входящее сообщение.
     * @return Строка для отображения в уведомлении (может быть пустой или null).
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
            case CHAT_UPDATE_TEMP_ID:
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
