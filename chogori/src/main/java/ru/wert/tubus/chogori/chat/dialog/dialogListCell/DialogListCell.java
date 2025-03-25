package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
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

    // Стили для входящих и исходящих сообщений
    static final String OUT = "message_out"; // Стиль исходящих сообщений
    static final String IN = "message_in";   // Стиль входящих сообщений

    // Пул потоков для фонового рендеринга сообщений (4 потока)
    private static final ExecutorService renderExecutor = Executors.newFixedThreadPool(4);

    // Основные компоненты ячейки
    private final MessageManager messageManager; // Менеджер для форматирования сообщений
    private final ConcurrentHashMap<Message, Parent> messageCache = new ConcurrentHashMap<>(); // Кэш сообщений
    private Parent separatorCache; // Кэш для разделителей чата
    private final StackPane container = new StackPane(); // Основной контейнер ячейки
    private final Rectangle whiteCurtain = new Rectangle(); // "Шторка" для анимации загрузки
    private FadeTransition fadeOut; // Анимация исчезновения шторки
    private final ListView<Message> listView; // Ссылка на родительский ListView
    private Message currentMessage; // Текущее отображаемое сообщение
    private final MessageContextMenu contextMenu;

    // Элементы контекстного меню
    private final MenuItem copyItem = new MenuItem("Копировать"); // Копировать текст
    private final MenuItem deleteItem = new MenuItem("Удалить");  // Удалить сообщение
    private final MenuItem forwardItem = new MenuItem("Переслать"); // Переслать сообщение
    private final MenuItem editItem = new MenuItem("Изменить");   // Редактировать сообщение

    /**
     * Конструктор ячейки
     * @param room Комната чата
     * @param listView Родительский ListView
     */
    public DialogListCell(Room room, ListView<Message> listView) {
        this.messageManager = new MessageManager(room);
        this.listView = listView;

        this.contextMenu = new MessageContextMenu(
                this::handleDeleteAction,
                this::handleForwardAction,
                this::handleEditAction
        );

        // Инициализация компонентов
        initContainer();
        initWhiteCurtain();
        initFadeTransition();
        setContextMenu(contextMenu.getContextMenu());
    }

    /**
     * Инициализация основного контейнера ячейки
     */
    private void initContainer() {
        container.setStyle("-fx-background-color: transparent;"); // Прозрачный фон
        container.setMaxHeight(Double.MAX_VALUE); // Максимальная возможная высота
    }

    /**
     * Инициализация "шторки" для анимации загрузки
     */
    private void initWhiteCurtain() {
        whiteCurtain.setFill(Color.WHITE); // Белый цвет
        whiteCurtain.setVisible(false);   // Изначально невидима
        // Привязка размеров к размерам ListView (с учетом полосы прокрутки)
        whiteCurtain.widthProperty().bind(listView.widthProperty().subtract(getVerticalScrollbarWidth()));
        whiteCurtain.heightProperty().bind(heightProperty());
    }

    /**
     * Инициализация анимации исчезновения шторки
     */
    private void initFadeTransition() {
        fadeOut = new FadeTransition(Duration.millis(200), whiteCurtain);
        fadeOut.setFromValue(1.0);    // Начальная прозрачность (полностью видима)
        fadeOut.setToValue(0.0);      // Конечная прозрачность (полностью прозрачна)
        fadeOut.setOnFinished(e -> whiteCurtain.setVisible(false)); // После завершения скрыть шторку
    }

    /**
     * Обновление содержимого ячейки
     * @param message Сообщение для отображения
     * @param empty Флаг пустой ячейки
     */
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        // Очистка ячейки, если она пустая
        if (empty || message == null) {
            clearCell();
            return;
        }

        // Если сообщение не изменилось, ничего не делаем
        if (message.equals(currentMessage)) {
            return;
        }

        // Обновление текущего сообщения и связанных компонентов
        currentMessage = message;
        contextMenu.setCurrentMessage(message); // Обновляем текущее сообщение в меню
        showWhiteCurtain();           // Показ анимации загрузки
        renderMessageInBackground(message); // Фоновый рендеринг сообщения
    }

    /**
     * Очистка ячейки
     */
    private void clearCell() {
        currentMessage = null;
        setText(null);
        setGraphic(null);
    }

    /**
     * Показ анимации загрузки (белой шторки)
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
     * Рендеринг сообщения в фоновом потоке
     * @param message Сообщение для рендеринга
     */
    private void renderMessageInBackground(Message message) {
        // Проверка актуальности сообщения
        if (!message.equals(currentMessage)) return;

        // Создание задачи для фонового рендеринга
        Task<Parent> renderTask = new Task<Parent>() {
            @Override
            protected Parent call() {
                return getCellNodeForMessage(message);
            }
        };

        // Обработка успешного завершения рендеринга
        renderTask.setOnSucceeded(e -> {
            if (!message.equals(currentMessage)) return;

            Parent renderedNode = renderTask.getValue();
            if (renderedNode != null) {
                Platform.runLater(() -> {
                    if (!message.equals(currentMessage)) return;

                    // Обновление содержимого ячейки
                    container.getChildren().clear();
                    container.getChildren().add(renderedNode);
                    setGraphic(container);

                    // Анимация появления сообщения
                    animateMessageAppearance(renderedNode);
                    fadeOut.play(); // Запуск анимации исчезновения шторки
                });
            }
        });

        // Обработка отмены задачи
        renderTask.setOnCancelled(e -> {
            if (!isVisible()) {
                Platform.runLater(() -> setGraphic(null));
            }
        });

        // Запуск задачи в пуле потоков
        renderExecutor.execute(renderTask);
    }

    /**
     * Анимация плавного появления сообщения
     * @param node Узел с сообщением
     */
    private void animateMessageAppearance(Parent node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), node);
        fadeIn.setFromValue(0);   // Начальная прозрачность (полностью прозрачен)
        fadeIn.setToValue(1);     // Конечная прозрачность (полностью видим)
        fadeIn.play();            // Запуск анимации
    }

    /**
     * Получение узла для отображения сообщения (с использованием кэша)
     * @param message Сообщение
     * @return Визуальный узел для отображения
     */
    private Parent getCellNodeForMessage(Message message) {
        // Обработка разделителя чата (специальный тип сообщения)
        if (message.getType() == Message.MessageType.CHAT_SEPARATOR) {
            synchronized (this) {
                if (separatorCache == null) {
                    separatorCache = MessageRenderer.mountSeparator(message);
                }
                return separatorCache;
            }
        }

        // Проверка кэша сообщений
        Parent cachedNode = messageCache.get(message);
        if (cachedNode != null && !isMessageChanged(message, cachedNode)) {
            return cachedNode;
        }

        // Создание нового узла для сообщения
        boolean isOutgoing = isOutgoingMessage(message);
        Parent newNode = messageManager.formatMessage(message, isOutgoing ? OUT : IN);
        messageCache.put(message, newNode);

        return newNode;
    }

    // Обработчики действий контекстного меню ================================================================

    /**
     * Обработчик удаления сообщения (заглушка)
     */
    private void handleDeleteAction() {
        if (currentMessage == null) return;
        log.debug("Удаление сообщения: {}", currentMessage.getId());
    }

    /**
     * Обработчик пересылки сообщения (заглушка)
     */
    private void handleForwardAction() {
        if (currentMessage == null) return;
        log.debug("Пересылка сообщения: {}", currentMessage.getId());
    }

    /**
     * Обработчик редактирования сообщения (заглушка)
     */
    private void handleEditAction() {
        if (currentMessage == null || currentMessage.getType() != Message.MessageType.CHAT_TEXT) return;
        log.debug("Редактирование сообщения: {}", currentMessage.getId());
    }

    // Вспомогательные методы  ======================================================================================

    /**
     * Получение ширины вертикальной полосы прокрутки
     * @return Ширина полосы прокрутки или 0, если не найдена
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
     * Проверка, является ли сообщение исходящим
     * @param msg Сообщение для проверки
     * @return true если сообщение от текущего пользователя
     */
    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    /**
     * Проверка, изменилось ли сообщение (заглушка)
     * @param message Сообщение
     * @param cachedNode Кэшированный узел
     * @return Всегда false (требует реализации)
     */
    private boolean isMessageChanged(Message message, Parent cachedNode) {
        return false;
    }

    /**
     * Завершение работы пула потоков
     */
    public static void shutdown() {
        renderExecutor.shutdown();
    }
}