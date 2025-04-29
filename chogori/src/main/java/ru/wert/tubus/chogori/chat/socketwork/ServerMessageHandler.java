package ru.wert.tubus.chogori.chat.socketwork;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.chogori.chat.socketwork.recievedMessageHandlers.*;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.serviceREST.RoomService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;
import ru.wert.tubus.client.utils.MessageStatus;
import ru.wert.tubus.client.utils.MessageType;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.*;
import static ru.wert.tubus.chogori.components.BtnChat.CHAT_OPEN;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.*;
import static ru.wert.tubus.chogori.statics.AppStatic.SIDE_CHAT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_NOTIFICATION;

/**
 * Основной обработчик входящих сообщений от сервера чата.
 * Обеспечивает асинхронную обработку сообщений и их распределение по соответствующим обработчикам.
 */
@Slf4j
public class ServerMessageHandler {

    private static final ExecutorService messageProcessingExecutor =
            Executors.newFixedThreadPool(4);

    public static RoomsController roomsController;
    public static DialogController dialogController;

    /**
     * Точка входа для обработки входящих сообщений от сервера.
     * @param message Входящее сообщение
     */
    public static void handle(Message message) {
        if (message == null) return;

        log.info("Получено сообщение от сервера: {}", message.toUsefulString());

        // Обработка в фоновом потоке
        messageProcessingExecutor.execute(() -> {
            try {
                if (message.getType() == MessageType.CHAT_UPDATE_TEMP_ID) {
                    handleTempIdUpdate(message);
                    return;
                }

                // Обработка уведомлений и сообщений

                Platform.runLater(()->{
                    String notificationText = delegateToMessageHandlers(message);
                    showNotificationIfNeeded(message, notificationText);
                });

                // Специальная обработка сообщений чата
                if (isChatRelatedMessage(message.getType())) {
                    processChatMessage(message);
                }
            } catch (Exception e) {
                log.error("Ошибка при обработке сообщения: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Делегирует обработку сообщения специализированным обработчикам.
     * @return Текст для уведомления
     */
    private static String delegateToMessageHandlers(Message message) {
        StringBuilder notificationText = new StringBuilder();
        MessageType type = message.getType();

        switch (type) {
            case USER_IN:
            case USER_OUT:
                UserStatusHandler.handle(message, type, notificationText, roomsController);
                break;
            case CHAT_TEXT:
            case CHAT_PASSPORTS:
            case CHAT_DRAFTS:
            case CHAT_FOLDERS:
            case CHAT_PICS:
                ChatMessageHandler.handle(message);
                break;
            case MESSAGE_DELIVERED:
                updateMessageStatus(message, MessageStatus.DELIVERED);
                break;
            case UPDATE_MESSAGE:
                updateMessageInOpenRooms(message);
                break;
            case DELETE_MESSAGE:
                deleteMessageFromOpenRooms(message);
                break;
            case UPDATE_PASSPORT:
                PassportMessageHandler.handle(message, type, notificationText);
                break;
            case ADD_DRAFT:
            case UPDATE_DRAFT:
            case DELETE_DRAFT:
                DraftMessageHandler.handle(message, type, notificationText);
                break;
            case ADD_FOLDER:
            case UPDATE_FOLDER:
            case DELETE_FOLDER:
                FolderMessageHandler.handle(message, type, notificationText);
                break;
            case ADD_PRODUCT:
            case UPDATE_PRODUCT:
            case DELETE_PRODUCT:
                ProductMessageHandler.handle(message, type, notificationText);
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

        return notificationText.toString();
    }

    /**
     * Показывает уведомление если выполнены условия
     */
    private static void showNotificationIfNeeded(Message message, String text) {
        if (text != null && !text.isEmpty() &&
                SP_NOTIFICATION != null && CH_SHOW_NOTIFICATION_LINE) {

            Platform.runLater(() -> {
                boolean isAdmin = CH_CURRENT_USER.getUserGroup().isAdministrate();
                boolean isUserStatus = message.getType() == MessageType.USER_IN ||
                        message.getType() == MessageType.USER_OUT;

                if (!isUserStatus || isAdmin) {
                    SP_NOTIFICATION.setText(text);
                }
            });
        }
    }

    /**
     * Обрабатывает сообщения чата (текст, изображения и т.д.)
     */
    private static void processChatMessage(Message message) {
        Message messageToProcess = extractChatMessageContent(message);
        if (messageToProcess == null || !isChatMessage(messageToProcess.getType())) return;

        Platform.runLater(() -> {
            // Добавляем комнату если её нет
            addRoomIfMissing(messageToProcess);

            // Показываем уведомление если нужно
            if (shouldNotifyAboutMessage(messageToProcess)) {
                addToUnreadAndNotify(messageToProcess);
            }

            // Доставляем сообщение в соответствующий диалог
            deliverToDialog(messageToProcess);
        });
    }

    /**
     * Извлекает содержимое сообщения чата (для PUSH-сообщений)
     */
    private static Message extractChatMessageContent(Message message) {
        if (message.getType() == MessageType.PUSH) {
            try {
                Gson gson = GsonConfiguration.createGson();
                return gson.fromJson(message.getText(), Message.class);
            } catch (Exception e) {
                log.error("Ошибка парсинга PUSH-сообщения: {}", e.getMessage());
                return null;
            }
        }
        return message;
    }

    /**
     * Добавляет комнату в список если её там нет
     */
    private static void addRoomIfMissing(Message message) {
        Room room = CH_ROOMS.findById(message.getRoomId());
        if (roomsController != null &&
                !roomsController.getListOfRooms().getItems().contains(room)) {
            roomsController.addRoomIfAbsent(room);
            roomsController.getListOfRooms().refresh();
        }
    }

    /**
     * Проверяет нужно ли показывать уведомление о сообщении
     */
    private static boolean shouldNotifyAboutMessage(Message message) {
        return !CHAT_OPEN ||
                !isRoomOpen(message.getRoomId()) ||
                isAppMinimized() ||
                isChatRoomPaneOnTop();
    }

    /**
     * Добавляет сообщение в список непрочитанных и показывает уведомление
     */
    private static void addToUnreadAndNotify(Message message) {
        if (!UNREAD_MESSAGES.contains(message)) {
            UNREAD_MESSAGES.add(message);
            PushNotification.show(message);
            if (roomsController != null) {
                roomsController.getListOfRooms().refresh();
            }
        }
    }

    /**
     * Доставляет сообщение в соответствующий диалог
     */
    private static void deliverToDialog(Message message) {
        for (DialogListView dialog : DialogController.openRooms.keySet()) {
            if (dialog.getRoom().getId().equals(message.getRoomId())) {
                dialog.receiveMessageFromServer(message);
                break;
            }
        }
    }

    /**
     * Обновляет статус сообщения на указанный
     */
    private static void updateMessageStatus(Message statusMessage, MessageStatus newStatus) {
        try {
            Gson gson = GsonConfiguration.createGson();
            Message targetMessage = gson.fromJson(statusMessage.getText(), Message.class);

            Platform.runLater(() -> {
                for (DialogListView dialog : DialogController.openRooms.keySet()) {
                    if (dialog.getRoom().getId().equals(targetMessage.getRoomId())) {
                        dialog.getItems().stream()
                                .filter(m -> m.getId() != null && m.getId().equals(targetMessage.getId()))
                                .findFirst()
                                .ifPresent(msg -> msg.setStatus(newStatus));
                        break;
                    }
                }
            });
        } catch (Exception e) {
            log.error("Ошибка обновления статуса сообщения: {}", e.getMessage());
        }
    }

    /**
     * Обрабатывает обновление временного ID сообщения
     */
    private static void handleTempIdUpdate(Message message) {
        Room room = RoomService.getInstance().findById(message.getRoomId());
        if (room == null) {
            log.warn("Комната для обновления ID не найдена");
            return;
        }

        DialogListView dialog = dialogController.findDialogForRoom(room);
        if (dialog == null) {
            log.warn("Диалог для комнаты {} не найден", room.getId());
            return;
        }

        // Находим и обновляем сообщение с временным ID
        dialog.getItems().stream()
                .filter(m -> m.getTempId() != null && m.getTempId().equals(message.getTempId()))
                .findFirst()
                .ifPresent(msg -> {
                    msg.setId(message.getId());
                    Platform.runLater(() -> {
                        dialog.refresh();
                        dialog.scrollTo(msg);
                    });
                });
    }

    /**
     * Проверяет относится ли сообщение к чату
     */
    private static boolean isChatRelatedMessage(MessageType type) {
        return type == MessageType.PUSH || type.name().startsWith("CHAT_");
    }

    /**
     * Проверяет является ли сообщение сообщением чата
     */
    private static boolean isChatMessage(MessageType type) {
        return type.name().startsWith("CHAT_");
    }

    /**
     * Проверяет открыта ли комната с указанным ID
     */
    public static boolean isRoomOpen(Long roomId) {
        Room currentRoom = dialogController != null ? dialogController.getCurrentOpenRoom() : null;
        return currentRoom != null && currentRoom.getId().equals(roomId);
    }

    /**
     * Проверяет свернуто ли приложение
     */
    public static boolean isAppMinimized() {
        return WinformStatic.WF_MAIN_STAGE != null &&
                WinformStatic.WF_MAIN_STAGE.isIconified();
    }

    /**
     * Проверяет находится ли панель комнат поверх панели диалога
     */
    public static boolean isChatRoomPaneOnTop() {
        StackPane mainChatPane = SIDE_CHAT.getMainChatPane();
        Parent chatRoomPane = SIDE_CHAT.getChatRooms();
        Parent chatDialogPane = SIDE_CHAT.getChatDialog();

        ObservableList<Node> children = mainChatPane.getChildren();
        int indexChatRoom = children.indexOf(chatRoomPane);
        int indexChatDialog = children.indexOf(chatDialogPane);

        return indexChatRoom > indexChatDialog;
    }
}
