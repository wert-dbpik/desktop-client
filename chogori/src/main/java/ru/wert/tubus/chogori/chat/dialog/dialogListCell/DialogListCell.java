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
    private static final String OUT = "message_out";
    private static final String IN = "message_in";
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);

    private final MessageManager messageManager;
    private final ConcurrentHashMap<String, Parent> messageCache = new ConcurrentHashMap<>();
    private Parent separatorCache;
    private final StackPane container = new StackPane();
    private final ListView<Message> listView;
    private Message currentMessage;
    private final MessageContextMenu contextMenu;

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

        // Добавляем слушатель изменения размера списка для автоматической прокрутки
        listView.getItems().addListener((javafx.collections.ListChangeListener<Message>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    Platform.runLater(() -> {
                        // Добавляем небольшую задержку для гарантированной прокрутки
                        new Thread(() -> {
                            try {
                                Thread.sleep(50); // Небольшая задержка
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            Platform.runLater(this::smartScrollToLastMessage);
                        }).start();
                    });
                }
            }
        });
    }

    private void initContainer() {
        container.setStyle("-fx-background-color: transparent;");
        container.setMaxHeight(Double.MAX_VALUE);
    }

    // Умная прокрутка с дополнительными проверками
    private void smartScrollToLastMessage() {
        if (listView == null || listView.getItems().isEmpty()) return;

        int lastIndex = listView.getItems().size() - 1;
        Message lastMessage = listView.getItems().get(lastIndex);

        // Прокручиваем только если это новое сообщение или последнее видимое
        if (shouldScrollToMessage(lastMessage)) {
            listView.scrollTo(lastIndex);
            // Дополнительная прокрутка через layout() для гарантии
            Platform.runLater(() -> {
                listView.layout();
                listView.scrollTo(lastIndex);
            });
        }
    }

    // Проверяем, нужно ли прокручивать к этому сообщению
    private boolean shouldScrollToMessage(Message message) {
        // Прокручиваем если:
        // 1. Это исходящее сообщение (от текущего пользователя)
        // 2. Или список уже был прокручен близко к концу
        return isOutgoingMessage(message) || isListNearBottom();
    }

    // Проверяем, находится ли список близко к концу
    private boolean isListNearBottom() {
        if (listView == null || listView.getItems().isEmpty()) return false;

        ScrollBar vbar = getVerticalScrollbar();
        if (vbar == null) return true;

        // Считаем что "близко к концу" - это последние 3 сообщения
        double position = vbar.getValue();
        double max = vbar.getMax();
        return (max - position) < 0.3; // 30% от конца
    }

    // Получаем вертикальный скроллбар
    private ScrollBar getVerticalScrollbar() {
        for (javafx.scene.Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                return (ScrollBar) node;
            }
        }
        return null;
    }

    // Метод для прокрутки к последнему сообщению
    private void scrollToLastMessage() {
        if (listView.getItems().size() > 0) {
            int lastIndex = listView.getItems().size() - 1;
            listView.scrollTo(lastIndex);
        }
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

                    // Прокрутка к новому сообщению, если оно последнее
                    if (listView.getItems().indexOf(message) == listView.getItems().size() - 1) {
                        smartScrollToLastMessage();
                    }
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
        return messageManager.formatMessage(message, isOutgoing ? OUT : IN);
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
                    Platform.runLater(()->scrollToLastMessage());// Прокрутка после отправки

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