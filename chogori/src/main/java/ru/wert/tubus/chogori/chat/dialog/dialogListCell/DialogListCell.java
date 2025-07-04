package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.utils.MessageType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DialogListCell extends ListCell<Message> {
    public static final String OUT = "message_out";
    public static final String IN = "message_in";
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);

    // Кэш для хранения уже отрендеренных сообщений
    private static final ConcurrentHashMap<String, Parent> messageCache = new ConcurrentHashMap<>();

    private final MessageCardsManager manager;
    private final StackPane container;
    private final ListView<Message> listView;
    private final DialogController dialogController;
    private final MessageContextMenu contextMenu;
    private Message currentMessage;
    private DialogListView dialogListView;
    private boolean isRendering = false;

    public DialogListCell(Room room, ListView<Message> listView, DialogController dialogController, DialogListView dialogListView) {
        this.manager = new MessageCardsManager(room);
        this.listView = listView;
        this.dialogController = dialogController;
        this.dialogListView = dialogListView;

        this.container = new StackPane();
        container.setStyle("-fx-background-color: transparent; -fx-padding: 0px 10px;");
        container.setMaxHeight(Double.MAX_VALUE);

        this.contextMenu = new MessageContextMenu(
                this::handleDeleteMessage,
                this::handleForwardMessage,
                this::handleUpdateMessage
        );

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(container);
        setContextMenu(contextMenu.getContextMenu());
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        // Очищаем текущее содержимое
        clearContent();

        if (empty || message == null) {
            return;
        }

        // Фиксируем текущее сообщение
        currentMessage = message;
        contextMenu.setCurrentMessage(message);

        String cacheKey = getCacheKey(message);
        Parent cachedNode = messageCache.get(cacheKey);

        if (cachedNode != null) {
            // Проверяем, что кэшированный узел актуален
            if (cachedNode.getUserData() instanceof MessageCardsManager) {
                MessageCardsManager manager = (MessageCardsManager) cachedNode.getUserData();
                manager.unbindCurrentMessage();
                manager.bindMessage(message, isOutgoingMessage(message) ? OUT : IN);
            }
            container.getChildren().setAll(cachedNode);
        } else {
            renderMessageInBackground(message);
        }
    }

    private String getCacheKey(Message message) {
        // Учитываем временный ID, если есть, иначе используем постоянный ID
        String idPart = message.getTempId() != null ?
                "temp_" + message.getTempId() :
                "perm_" + (message.getId() != null ? message.getId().toString() : "null");

        // Добавляем тип сообщения, хэш текста и статус
        return idPart + "_" +
                message.getType() + "_" +
                (message.getText() != null ? message.getText().hashCode() : 0) + "_" +
                message.getStatus();
    }

    public static void clearCacheForMessage(Message message) {
        if (message == null) return;

        // Удаляем все возможные варианты ключей для этого сообщения
        String tempKey = "temp_" + message.getTempId() + "_" +
                message.getType() + "_" +
                (message.getText() != null ? message.getText().hashCode() : 0) + "_" +
                message.getStatus();

        String permKey = "perm_" + (message.getId() != null ? message.getId().toString() : "null") + "_" +
                message.getType() + "_" +
                (message.getText() != null ? message.getText().hashCode() : 0) + "_" +
                message.getStatus();

        messageCache.remove(tempKey);
        messageCache.remove(permKey);
    }

    private void clearContent() {
        // Всегда очищаем контейнер, независимо от состояния рендеринга
        container.getChildren().clear();
        if (manager != null) {
            manager.unbindCurrentMessage();
        }
        currentMessage = null;
    }

    private void renderMessageInBackground(Message message) {
        if (isRendering || message == null || !message.equals(currentMessage)) {
            return;
        }

        isRendering = true;
        final Message messageToRender = message;

        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return createMessageNode(messageToRender);
            }

            @Override
            protected void succeeded() {
                isRendering = false;
                Parent renderedNode = getValue();
                if (renderedNode != null && messageToRender.equals(currentMessage)) {
                    String cacheKey = getCacheKey(messageToRender);
                    messageCache.put(cacheKey, renderedNode);
                    Platform.runLater(() -> {
                        if (!isEmpty() && messageToRender.equals(getItem())) {
                            container.getChildren().setAll(renderedNode);
                            animateMessageAppearance(renderedNode);
                        }
                    });
                }
            }

            @Override
            protected void failed() {
                isRendering = false;
                log.error("Ошибка при рендеринге сообщения {}", messageToRender.getId(), getException());
            }
        };
        renderExecutor.execute(renderTask);
    }

    private Parent createMessageNode(Message message) {
        if (message.getType() == MessageType.CHAT_SEPARATOR) {
            return MessageCardsRenderer.mountSeparator(message);
        }

        boolean isOutgoing = isOutgoingMessage(message);
        manager.bindMessage(message, isOutgoing ? OUT : IN);
        Parent view = (Parent) manager.getView();
        view.setUserData(manager); // Сохраняем ссылку на менеджер для обновления статуса
        return view;
    }

    private void animateMessageAppearance(Parent node) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void handleDeleteMessage() {
        if (currentMessage == null) return;
        contextMenu.deleteMessage(currentMessage, listView);
//        dialogListView.logAllMessagesOrdered();
    }

    private void handleForwardMessage() {
        if (currentMessage == null) return;
        contextMenu.forwardMessage(currentMessage, listView);
    }

    private void handleUpdateMessage() {
        if (currentMessage == null || currentMessage.getType() != MessageType.CHAT_TEXT) return;

        Platform.runLater(() -> {
            dialogController.getTaMessageText().setText(currentMessage.getText());
            dialogController.getTaMessageText().requestFocus();
            dialogController.getTaMessageText().positionCaret(currentMessage.getText().length());

            dialogController.getBtnSend().setOnAction(e -> {
                String updatedText = dialogController.getTaMessageText().getText().trim();
                if (!updatedText.isEmpty()) {
                    contextMenu.updateMessage(currentMessage, updatedText, listView);
                    dialogController.getBtnSend().setOnAction(event -> dialogListView.sendText());
                    dialogController.getTaMessageText().clear();
                }
            });
        });
    }

    private boolean isOutgoingMessage(Message msg) {
        if (msg == null || ChogoriSettings.CH_CURRENT_USER == null) {
            return false;
        }

        try {
            long senderId = msg.getSenderId();
            long currentUserId = ChogoriSettings.CH_CURRENT_USER.getId();
            return senderId == currentUserId;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        // Отключаем стандартное выделение
    }

    public static void clearCache() {
        messageCache.clear();
    }

    public static void shutdown() {
        renderExecutor.shutdown();
        clearCache();
    }
}