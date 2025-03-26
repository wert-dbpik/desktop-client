package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

/**
 * Упрощенная версия DialogListCell без анимаций для слабых компьютеров.
 * Сохранены:
 * - Контекстное меню и его обработчики
 * - Логика входящих/исходящих сообщений
 * Упрощения:
 * - Нет анимаций
 * - Нет кэширования сообщений
 * - Рендеринг в основном потоке
 */
public class DialogListCell extends ListCell<Message> {

    private final MessageManager messageManager;
    private final ListView<Message> listView;
    private final DialogController dialogController;
    private Message currentMessage;
    private final MessageContextMenu contextMenu;

    // Стили для входящих и исходящих сообщений
    static final String OUT = "message_out";
    static final String IN = "message_in";

    public DialogListCell(Room room, ListView<Message> listView, DialogController dialogController) {
        this.messageManager = new MessageManager(room);
        this.listView = listView;
        this.dialogController = dialogController;

        this.contextMenu = new MessageContextMenu(
                this::handleDeleteMessageAction,
                this::handleForwardMessageAction,
                this::handleUpdateMessageAction
        );

        setContextMenu(contextMenu.getContextMenu());
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            clearCell();
            return;
        }

        if (message.equals(currentMessage)) {
            return;
        }

        currentMessage = message;
        contextMenu.setCurrentMessage(message);
        renderMessage(message);
    }

    private void clearCell() {
        currentMessage = null;
        setText(null);
        setGraphic(null);
    }

    private void renderMessage(Message message) {
        boolean isOutgoing = isOutgoingMessage(message);
        VBox messageNode = messageManager.formatMessage(message, isOutgoing ? OUT : IN);

        Platform.runLater(() -> {
            setText(null);
            setGraphic(messageNode);
        });
    }

    // Обработчики контекстного меню -----------------------------------------------------

    private void handleDeleteMessageAction() {
        if (currentMessage == null) return;
        contextMenu.deleteMessage(currentMessage, listView);
    }

    private void handleForwardMessageAction() {
        if (currentMessage == null) return;
        contextMenu.forwardMessage(currentMessage, listView);
    }

    private void handleUpdateMessageAction() {
        if (currentMessage == null || currentMessage.getType() != Message.MessageType.CHAT_TEXT) return;

        dialogController.getTaMessageText().setText(currentMessage.getText());
        dialogController.getTaMessageText().requestFocus();
        dialogController.getTaMessageText().positionCaret(currentMessage.getText().length());

        Button btnSend = dialogController.getBtnSend();
        btnSend.setOnAction(e -> {
            String updatedText = dialogController.getTaMessageText().getText().trim();
            if (!updatedText.isEmpty()) {
                contextMenu.updateMessage(currentMessage, updatedText, listView);
                btnSend.setOnAction(event -> dialogController.getDialogListView().sendText());
                dialogController.getTaMessageText().clear();
            }
        });
    }

    // Вспомогательные методы ------------------------------------------------------------

    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    /**
     * Завершение работы (для совместимости с DialogListCell)
     */
    public static void shutdown() {
        // В упрощенной версии не требуется закрывать пул потоков,
        // так как он не используется, но метод оставлен для совместимости
    }
}