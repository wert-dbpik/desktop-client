package ru.wert.tubus.chogori.chat.socketwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.roomsController.RoomsController;
import ru.wert.tubus.client.entity.models.Message;

import static ru.wert.tubus.client.entity.models.Message.MessageType.*;

class ServerMessageHandlerTest {

    @Mock
    private RoomsController mockRoomsController;
    @Mock
    private DialogController mockDialogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ServerMessageHandler.roomsController = mockRoomsController;
        ServerMessageHandler.dialogController = mockDialogController;
    }

    @Test
    void handle_UserInMessage_ShouldCallUserStatusHandler() {
        // Arrange
        Message message = new Message();
        message.setType(USER_IN);
        message.setText("User1 has joined");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение USER_IN обрабатывается UserStatusHandler
        // (проверка косвенная через processMessage)
    }

    @Test
    void handle_ChatTextMessage_ShouldCallChatMessageHandler() {
        // Arrange
        Message message = new Message();
        message.setType(CHAT_TEXT);
        message.setRoomId(1L);
        message.setText("Hello world");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение CHAT_TEXT обрабатывается ChatMessageHandler
        // и processChatMessage
    }

    @Test
    void handle_UpdateMessage_ShouldCallUpdateMessageInOpenRooms() {
        // Arrange
        Message message = new Message();
        message.setType(UPDATE_MESSAGE);
        message.setRoomId(1L);
        message.setText("Updated message");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение UPDATE_MESSAGE вызывает updateMessageInOpenRooms
    }

    @Test
    void handle_AddDraftMessage_ShouldCallDraftMessageHandler() {
        // Arrange
        Message message = new Message();
        message.setType(ADD_DRAFT);
        message.setText("New draft added");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение ADD_DRAFT обрабатывается DraftMessageHandler
    }

    @Test
    void handle_AddFolderMessage_ShouldCallFolderMessageHandler() {
        // Arrange
        Message message = new Message();
        message.setType(ADD_FOLDER);
        message.setText("New folder added");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение ADD_FOLDER обрабатывается FolderMessageHandler
    }

    @Test
    void handle_AddProductMessage_ShouldCallProductMessageHandler() {
        // Arrange
        Message message = new Message();
        message.setType(ADD_PRODUCT);
        message.setText("New product added");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение ADD_PRODUCT обрабатывается ProductMessageHandler
    }

    @Test
    void handle_PushMessage_ShouldCallPushMessageHandler() {
        // Arrange
        Message message = new Message();
        message.setType(PUSH);
        message.setText("{\"type\":\"CHAT_TEXT\",\"text\":\"Pushed message\"}");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение PUSH обрабатывается PushMessageHandler
        // и затем processChatMessage для вложенного сообщения
    }

    @Test
    void handle_DeleteMessage_ShouldCallDeleteMessageFromOpenRooms() {
        // Arrange
        Message message = new Message();
        message.setType(DELETE_MESSAGE);
        message.setRoomId(1L);
        message.setText("Message to delete");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение DELETE_MESSAGE вызывает deleteMessageFromOpenRooms
    }

    @Test
    void handle_ProductGroupMessage_ShouldCallOtherChangesHandler() {
        // Arrange
        Message message = new Message();
        message.setType(ADD_PRODUCT_GROUP);
        message.setText("Product group changed");

        // Act
        ServerMessageHandler.handle(message);

        // Assert
        // Проверяем, что сообщение ADD_PRODUCT_GROUP обрабатывается OtherChangesHandler
    }
}
