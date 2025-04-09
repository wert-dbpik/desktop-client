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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DialogListCell extends ListCell<Message> {

    private final DialogController dialogController;
    private final MessageManager messageManager;
    private final ConcurrentHashMap<String, Parent> messageCache = new ConcurrentHashMap<>();
    private Parent separatorCache;
    private final StackPane container = new StackPane();
    private final ListView<Message> listView;
    private Message currentMessage;
    private final MessageContextMenu contextMenu;
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

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

        currentMessage = message;
        contextMenu.setCurrentMessage(message);

        renderMessageInBackground(message);
    }

    private void clearCell() {
        currentMessage = null;
        setText(null);
        setGraphic(null);
        container.getChildren().clear();
    }

    private void renderMessageInBackground(Message message) {
        // Генерируем временный ключ для сообщений без ID
        String cacheKey = message.getId() != null ?
                message.getId().toString() :
                "temp_" + UUID.randomUUID().toString();

        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return getCellNodeForMessage(message, cacheKey);
            }
        };

        renderTask.setOnSucceeded(e -> {
            Parent renderedNode = renderTask.getValue();
            if (renderedNode != null && message.equals(currentMessage)) {
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    container.getChildren().add(renderedNode);
                    setGraphic(container);
                    animateMessageAppearance(renderedNode);
                });
            }
        });

        renderTask.setOnFailed(e -> {
            log.error("Ошибка рендеринга сообщения: {}", e.getSource().getException().getMessage());
        });

        renderExecutor.execute(renderTask);
    }

    private Parent getCellNodeForMessage(Message message, String cacheKey) {
        if (message.getType() == Message.MessageType.CHAT_SEPARATOR) {
            synchronized (this) {
                if (separatorCache == null) {
                    separatorCache = MessageRenderer.mountSeparator(message);
                }
                return separatorCache;
            }
        }

        // Для сообщений с ID проверяем кэш
        if (message.getId() != null) {
            Parent cachedNode = messageCache.get(cacheKey);
            if (cachedNode != null) {
                return cachedNode;
            }
        }

        boolean isOutgoing = isOutgoingMessage(message);
        Parent newNode = messageManager.formatMessage(message, isOutgoing ? OUT : IN);

        // Кэшируем только сообщения с ID
        if (message.getId() != null) {
            messageCache.put(cacheKey, newNode);
        }

        return newNode;
    }

    private void animateMessageAppearance(Parent node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    // Обработчики контекстного меню остаются без изменений
    private void handleDeleteMessageAction() { /* ... */ }
    private void handleForwardMessageAction() { /* ... */ }
    private void handleUpdateMessageAction() { /* ... */ }

    public static void shutdown() {
        renderExecutor.shutdownNow();
    }
}