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

@Slf4j
public class DialogListCell extends ListCell<Message> {

    private final DialogController dialogController;
    private final MessageManager messageManager;
    private final ConcurrentHashMap<Long, Parent> messageCache = new ConcurrentHashMap<>(); // Изменено на хранение по ID
    private Parent separatorCache;
    private final StackPane container = new StackPane();
    private final ListView<Message> listView;
    private volatile Message currentMessage; // Добавлено volatile для thread-safety
    private final MessageContextMenu contextMenu;
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // Потоки-демоны для автоматического завершения
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

        // Очищаем ячейку перед обновлением
        if (empty || message == null) {
            clearCell();
            return;
        }

        // Проверяем, действительно ли сообщение изменилось
        if (message.equals(currentMessage)) {
            return;
        }

        // Обновляем текущее сообщение
        currentMessage = message;
        contextMenu.setCurrentMessage(message);

        // Очищаем предыдущее содержимое перед рендерингом нового
        Platform.runLater(() -> {
            container.getChildren().clear();
            setGraphic(container);
        });

        renderMessageInBackground(message);
    }

    private void clearCell() {
        currentMessage = null;
        setText(null);
        setGraphic(null);
        container.getChildren().clear();
    }

    private void renderMessageInBackground(Message message) {
        if(message == null) return;
        final String messageTempId = message.getTempId(); // Сохраняем ID для проверки актуальности

        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return getCellNodeForMessage(message);
            }
        };

        renderTask.setOnSucceeded(e -> {
            // Двойная проверка актуальности сообщения
            if (currentMessage == null || messageTempId != currentMessage.getTempId()) {
                return;
            }

            Parent renderedNode = renderTask.getValue();
            if (renderedNode != null) {
                Platform.runLater(() -> {
                    // Тройная проверка актуальности сообщения
                    if (currentMessage == null || messageTempId != currentMessage.getTempId()) {
                        return;
                    }

                    container.getChildren().clear();
                    container.getChildren().add(renderedNode);
                    setGraphic(container);
                    animateMessageAppearance(renderedNode);
                });
            }
        });

        renderTask.setOnFailed(e -> {
            log.error("Ошибка при рендеринге сообщения: {}", e.getSource().getException().getMessage());
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

        // Проверяем кэш по ID сообщения
        Parent cachedNode = messageCache.get(message.getId());
        if (cachedNode != null) {
            return cachedNode;
        }

        boolean isOutgoing = isOutgoingMessage(message);
        Parent newNode = messageManager.formatMessage(message, isOutgoing ? OUT : IN);
        messageCache.put(message.getId(), newNode); // Сохраняем по ID

        return newNode;
    }

    // Обработчики контекстного меню остаются без изменений
    private void handleDeleteMessageAction() { /* ... */ }
    private void handleForwardMessageAction() { /* ... */ }
    private void handleUpdateMessageAction() { /* ... */ }

    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    public static void shutdown() {
        renderExecutor.shutdownNow();
    }
}