package ru.wert.tubus.chogori.chat.socketwork.recievedMessageHandlers;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.serviceREST.RoomService;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Обработчик сообщений чата.
 * Теперь поддерживает:
 * 1. Обычные сообщения чата
 * 2. Обновление временных ID сообщений (CHAT_UPDATE_TEMP_ID)
 */
@Slf4j
public class ChatMessageHandler {

    /**
     * Основной метод обработки входящих сообщений
     * @param message Входящее сообщение от сервера
     */
    public static void handle(Message message) {
        // Проверяем, относится ли сообщение к текущему пользователю
        Long roomId = message.getRoomId();
        Room room = RoomService.getInstance().findById(roomId);

        if (room == null || !room.getRoommates().contains(CH_CURRENT_USER.getId())) {
            log.debug("Сообщение не предназначено текущему пользователю или комната не найдена");
            return;
        }

        processRegularChatMessage(message);

    }

    /**
     * Обрабатывает обычные сообщения чата
     * @param message Входящее сообщение
     */
    private static void processRegularChatMessage(Message message) {
        Room room = RoomService.getInstance().findById(message.getRoomId());
        if (room == null) {
            log.warn("Комната для сообщения не найдена");
            return;
        }

        // Добавляем комнату, если её нет
        ServerMessageHandler.roomsController.addRoomIfAbsent(room);

        DialogListView dialog = ServerMessageHandler.dialogController.findDialogForRoom(room);
        if (dialog != null) {
            // Добавляем сообщение в диалог
            Platform.runLater(()->{
                dialog.getItems().add(message);
                dialog.refresh();
                dialog.scrollTo(message);
            });

            // Обновляем статус сообщения
            message.setStatus(Message.MessageStatus.DELIVERED);
            CH_MESSAGES.update(message);

            log.debug("Добавлено новое сообщение {} в комнату {}", message.toUsefulString(), room.getId());
        }
    }
}
