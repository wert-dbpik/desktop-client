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
import ru.wert.tubus.client.utils.MessageType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DialogListCell extends ListCell<Message> {

    private final DialogController dialogController;
    public static final String OUT = "message_out";
    public static final String IN = "message_in";
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);

    private final MessageCardsManager messageCardsManager;
    private final ConcurrentHashMap<String, Parent> messageCache = new ConcurrentHashMap<>();
    private Parent separatorCache;
    private final StackPane container = new StackPane();
    private final ListView<Message> listView;
    private Message currentMessage;
    private final MessageContextMenu contextMenu;

    public DialogListCell(Room room, ListView<Message> listView, DialogController dialogController) {
        this.messageCardsManager = new MessageCardsManager(room);
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

    // Добавьте метод для очистки кэша
    public void clearCache() {
        messageCache.clear();
        separatorCache = null;
        log.debug("Кэш сообщений очищен");
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

        // Проверяем, действительно ли сообщение изменилось
        if (message.equals(currentMessage)) {
            // Если это то же сообщение, но изменился статус - обновляем
            if (currentMessage != null && !currentMessage.getStatus().equals(message.getStatus())) {
                updateStatus(message);
            }
            return;
        }

        currentMessage = message;
        contextMenu.setCurrentMessage(message);
        renderMessageInBackground(message);
    }

    private void updateStatus(Message message) {
        Platform.runLater(() -> {
            if (container.getChildren().isEmpty()) return;

            Parent renderedNode = (Parent) container.getChildren().get(0);
            if (renderedNode.getUserData() instanceof MessageCardsManager) {
                MessageCardsManager manager = (MessageCardsManager) renderedNode.getUserData();
                manager.updateMessageStatus(message.getStatus());
            }
        });
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
        if (message.getType() == MessageType.CHAT_SEPARATOR) {
            synchronized (this) {
                if (separatorCache == null) {
                    separatorCache = MessageCardsRenderer.mountSeparator(message);
                }
                return separatorCache;
            }
        }

        String cacheKey = message.getTempId() != null ? message.getTempId() : message.getId() != null ? message.getId().toString() : null;
        if (cacheKey == null) return createNewMessageNode(message);

        Parent cachedNode = messageCache.get(cacheKey);
        if (cachedNode != null) {
            return cachedNode;
        }

        Parent newNode = createNewMessageNode(message);
        messageCache.put(cacheKey, newNode);
        return newNode;
    }

    private Parent createNewMessageNode(Message message) {
        boolean isOutgoing = isOutgoingMessage(message);
        return messageCardsManager.formatMessage(message, isOutgoing ? OUT : IN);
    }

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
        if (currentMessage == null || currentMessage.getType() != MessageType.CHAT_TEXT) return;
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

    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    public static void shutdown() {
        renderExecutor.shutdown();
    }
}