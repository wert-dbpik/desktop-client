package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс DialogListCell представляет собой ячейку списка сообщений в чате.
 * Он отвечает за:
 * - Отображение сообщений с анимациями
 * - Кэширование уже отрисованных сообщений
 * - Обработку контекстного меню для сообщений
 * - Разделение входящих/исходящих сообщений
 */
@Slf4j
public class DialogListCell extends ListCell<Message> {

    private final DialogController dialogController;
    private final MessageManager messageManager;
    private final ConcurrentHashMap<Message, Parent> messageCache = new ConcurrentHashMap<>();
    private Parent separatorCache;
    private final StackPane container = new StackPane();
    private final ListView<Message> listView;
    private Message currentMessage;
    private final MessageContextMenu contextMenu;
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);

    // Стили для сообщений
    static final String OUT = "message_out";
    static final String IN = "message_in";

    // Элементы контекстного меню
    private final MenuItem copyItem = new MenuItem("Копировать");
    private final MenuItem deleteItem = new MenuItem("Удалить");
    private final MenuItem forwardItem = new MenuItem("Переслать");
    private final MenuItem editItem = new MenuItem("Изменить");

    public DialogListCell(Room room, ListView<Message> listView, DialogController dialogController) {
        this.messageManager = new MessageManager(room);
        this.listView = listView;
        this.dialogController = dialogController;

        this.contextMenu = new MessageContextMenu(
                this::handleDeleteMessageAction,
                this::handleForwardMessageAction,
                this::handleUpdateMessageAction
        );

        setStyle("-fx-padding: 0px 10px; -fx-background-insets: 0;");
        initContainer();
        setContextMenu(contextMenu.getContextMenu());
    }

    private void initContainer() {
        container.setStyle("-fx-background-color: transparent;");
        container.setMaxHeight(Double.MAX_VALUE);
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
        renderMessageInBackground(message);
    }

    private void clearCell() {
        currentMessage = null;
        setText(null);
        setGraphic(null);
    }

    private void renderMessageInBackground(Message message) {
        if (!message.equals(currentMessage)) return;

        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return getCellNodeForMessage(message);
            }
        };

        renderTask.setOnSucceeded(e -> {
            if (!message.equals(currentMessage)) return;

            Parent renderedNode = renderTask.getValue();
            if (renderedNode != null) {
                Platform.runLater(() -> {
                    if (!message.equals(currentMessage)) return;

                    container.getChildren().clear();
                    container.getChildren().add(renderedNode);
                    setGraphic(container);
                    animateMessageAppearance(renderedNode);
                });
            }
        });

        renderTask.setOnCancelled(e -> {
            if (!isVisible()) {
                Platform.runLater(() -> setGraphic(null));
            }
        });

        renderExecutor.execute(renderTask);
    }

    private void animateMessageAppearance(Parent node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private Parent getCellNodeForMessage(Message message) {
        if (message.getType() == Message.MessageType.CHAT_SEPARATOR) {
            synchronized (this) {
                if (separatorCache == null) {
                    separatorCache = MessageRenderer.mountSeparator(message);
                }
                return separatorCache;
            }
        }

        Parent cachedNode = messageCache.get(message);
        if (cachedNode != null && !isMessageChanged(message, cachedNode)) {
            return cachedNode;
        }

        boolean isOutgoing = isOutgoingMessage(message);
        Parent newNode = messageManager.formatMessage(message, isOutgoing ? OUT : IN);
        messageCache.put(message, newNode);

        return newNode;
    }

    // Обработчики контекстного меню
    private void handleDeleteMessageAction() {
        if (currentMessage == null) return;
        log.debug("Удаление сообщения: {}", currentMessage.getId());
        contextMenu.deleteMessage(currentMessage, listView);
    }

    private void handleForwardMessageAction() {
        if (currentMessage == null) return;
        log.debug("Пересылка сообщения: {}", currentMessage.getId());
        contextMenu.forwardMessage(currentMessage, listView);
    }

    private void handleUpdateMessageAction() {
        if (currentMessage == null || currentMessage.getType() != Message.MessageType.CHAT_TEXT) return;
        log.debug("Редактирование сообщения: {}", currentMessage.getId());

        Platform.runLater(() -> {
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
        });
    }

    // Вспомогательные методы
    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    private boolean isMessageChanged(Message message, Parent cachedNode) {
        return false;
    }

    public static void shutdown() {
        renderExecutor.shutdown();
    }
}