package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс DialogListCell представляет собой ячейку списка сообщений в чате.
 * Он отвечает за отображение сообщений, включая анимации и кэширование.
 */
public class DialogListCell extends ListCell<Message> {

    // Пул потоков для рендеринга сообщений в фоновом режиме
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);
    // Менеджер сообщений для форматирования и управления сообщениями
    private final MessageManager messageManager;
    // Кэш для хранения уже отрендеренных сообщений
    private final ConcurrentHashMap<Message, Parent> messageCache = new ConcurrentHashMap<>();
    // Кэш для разделителя сообщений
    private Parent separatorCache;

    // Контейнер для размещения элементов ячейки
    private final StackPane container = new StackPane();
    // Шторка для анимации загрузки
    private final Rectangle whiteCurtain = new Rectangle();
    // Анимация исчезновения шторки
    private final FadeTransition fadeOut;
    // Ссылка на ListView, к которому принадлежит ячейка
    private final ListView<Message> listView;
    // Текущее отображаемое сообщение
    private Message currentMessage;

    /**
     * Конструктор класса.
     *
     * @param room     Комната чата, к которой принадлежат сообщения.
     * @param listView ListView, содержащий ячейки сообщений.
     */
    public DialogListCell(Room room, ListView<Message> listView) {
        this.messageManager = new MessageManager(room);
        this.listView = listView;

        // Настройка контейнера
        container.setStyle("-fx-background-color: transparent;");
        container.setMaxHeight(Double.MAX_VALUE);

        // Настройка шторки
        whiteCurtain.setFill(Color.WHITE);
        whiteCurtain.setVisible(false);
        whiteCurtain.widthProperty().bind(listView.widthProperty().subtract(getVerticalScrollbarWidth()));
        whiteCurtain.heightProperty().bind(heightProperty());

        // Настройка анимации исчезновения шторки
        fadeOut = new FadeTransition(Duration.millis(200), whiteCurtain);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> whiteCurtain.setVisible(false));
    }

    /**
     * Метод для получения ширины вертикальной полосы прокрутки ListView.
     *
     * @return Ширина полосы прокрутки или 0, если полоса не найдена.
     */
    private double getVerticalScrollbarWidth() {
        for (javafx.scene.Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                return ((ScrollBar) node).getWidth();
            }
        }
        return 0;
    }

    /**
     * Метод обновления содержимого ячейки.
     *
     * @param message Сообщение для отображения.
     * @param empty   Флаг, указывающий, является ли ячейка пустой.
     */
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        // Очищаем предыдущее сообщение
        if (empty || message == null) {
            currentMessage = null;
            setText(null);
            setGraphic(null);
            return;
        }

        // Проверяем, не пытаемся ли отобразить то же сообщение повторно
        if (message.equals(currentMessage)) {
            return;
        }

        currentMessage = message;
        showWhiteCurtain();
        renderMessageInBackground(message);
    }

    /**
     * Метод для отображения шторки перед загрузкой сообщения.
     */
    private void showWhiteCurtain() {
        Platform.runLater(() -> {
            setText(null);
            whiteCurtain.setVisible(true);
            whiteCurtain.setOpacity(1.0);
            container.getChildren().clear();
            container.getChildren().add(whiteCurtain);
            setGraphic(container);
        });
    }

    /**
     * Метод для рендеринга сообщения в фоновом потоке.
     *
     * @param message Сообщение для рендеринга.
     */
    private void renderMessageInBackground(Message message) {
        // Проверяем, актуально ли еще это сообщение для текущей ячейки
        if (!message.equals(currentMessage)) {
            return;
        }

        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return getCellNodeForMessage(message);
            }
        };

        renderTask.setOnSucceeded(e -> {
            // Дополнительная проверка актуальности сообщения
            if (!message.equals(currentMessage)) {
                return;
            }

            Parent renderedNode = renderTask.getValue();
            if (renderedNode != null) {
                Platform.runLater(() -> {
                    // Еще одна проверка перед отображением
                    if (!message.equals(currentMessage)) {
                        return;
                    }

                    container.getChildren().clear();
                    container.getChildren().add(renderedNode);
                    setGraphic(container);

                    // Анимация плавного появления сообщения
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), renderedNode);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();

                    // Запуск анимации исчезновения шторки
                    fadeOut.play();
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

    /**
     * Метод для получения узла (Node) ячейки для сообщения.
     *
     * @param message Сообщение.
     * @return Узел, представляющий сообщение.
     */
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

    /**
     * Метод для проверки, является ли сообщение исходящим.
     *
     * @param msg Сообщение.
     * @return true, если сообщение исходящее, иначе false.
     */
    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    /**
     * Метод для проверки, изменилось ли сообщение (заглушка).
     *
     * @param message    Сообщение.
     * @param cachedNode Кэшированный узел.
     * @return Всегда возвращает false.
     */
    private boolean isMessageChanged(Message message, Parent cachedNode) {
        return false;
    }

    /**
     * Метод для завершения работы пула потоков.
     */
    public static void shutdown() {
        renderExecutor.shutdown();
    }

    // Константы для стилей сообщений
    static final String OUT = "message_out";
    static final String IN = "message_in";
}