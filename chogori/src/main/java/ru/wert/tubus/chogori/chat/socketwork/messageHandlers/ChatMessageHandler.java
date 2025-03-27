package ru.wert.tubus.chogori.chat.socketwork.messageHandlers;

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
 */
@Slf4j
public class ChatMessageHandler {
    public static void handle(Message message) {
        Long roomId = message.getRoomId();
        Room room = RoomService.getInstance().findById(roomId);
        if (room != null && room.getRoommates().contains(CH_CURRENT_USER.getId())) {
            ServerMessageHandler.roomsController.addRoomIfAbsent(room);
            processChatMessage(message);
        }
    }

    private static void processChatMessage(Message message) {
        Room room = RoomService.getInstance().findById(message.getRoomId());
        if (room != null) {
            DialogListView dialog = ServerMessageHandler.dialogController.findDialogForRoom(room);
            if (dialog != null) {
                dialog.getItems().add(message);
                dialog.refresh();
                dialog.scrollTo(message);

                message.setStatus(Message.MessageStatus.DELIVERED);
                CH_MESSAGES.update(message);
            }
        }
    }
}
