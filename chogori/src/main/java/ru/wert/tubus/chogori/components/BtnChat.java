package ru.wert.tubus.chogori.components;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler;
import ru.wert.tubus.chogori.chat.socketwork.ServiceMessaging;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.utils.MessageStatus;
import javafx.animation.Interpolator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController.closeAllRooms;
import static ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController.openOneRoom;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.getSecondUserInOneToOneChat;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_WHITE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_YELLOW_IMG;
import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_CHAT;

@Slf4j
public class BtnChat {

    // Изображения для кнопки чата
    private final ImageView imageWhite = new ImageView(CHAT_WHITE_IMG); // Обычное состояние
    private final ImageView imageYellow = new ImageView(CHAT_YELLOW_IMG); // Активное состояние (новые сообщения)

    // Флаг наличия новых сообщений
    private final BooleanProperty hasNewMessages = new SimpleBooleanProperty(false);

    // Анимация мигания кнопки
    private Timeline blinkTimeline;

    // Флаг состояния чата (открыт/закрыт)
    public static boolean CHAT_OPEN = false;

    // Основной компонент чата
    private final SideChat sideChat = new SideChat();

    // Пул потоков для обработки сообщений
    private final ExecutorService messageProcessingExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private final Button btnChat;

    /**
     * Конструктор кнопки чата.
     * @param button Кнопка, которая будет использоваться для управления чатом
     */
    public BtnChat(Button button) {
        this.btnChat = button;
        initializeButton();
        setupBlinkAnimation();
        setupChatToggleHandler();
    }

    /**
     * Инициализация кнопки: настройка внешнего вида и базовых свойств.
     */
    private void initializeButton() {
        btnChat.setGraphic(imageWhite);
        btnChat.setTooltip(new Tooltip("Открыть чат"));
        btnChat.setStyle("-fx-background-color: rgb(50, 50, 50); -fx-fit-to-width: 16pt; -fx-fit-to-height: 16pt");
    }

    /**
     * Настройка анимации мигания кнопки при новых сообщениях.
     */
    private void setupBlinkAnimation() {
        blinkTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> toggleButtonImage())
        );
        blinkTimeline.setCycleCount(Timeline.INDEFINITE);

        // Связываем анимацию с состоянием hasNewMessages
        hasNewMessages.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                blinkTimeline.play();
            } else {
                blinkTimeline.stop();
                btnChat.setGraphic(imageWhite);
            }
        });
    }

    /**
     * Переключение изображения кнопки (для анимации мигания).
     */
    private void toggleButtonImage() {
        Platform.runLater(() -> {
            if (btnChat.getGraphic().equals(imageWhite)) {
                btnChat.setGraphic(imageYellow);
            } else {
                btnChat.setGraphic(imageWhite);
            }
        });
    }

    /**
     * Настройка обработчика нажатия на кнопку чата.
     */
    private void setupChatToggleHandler() {
        btnChat.setOnAction(e -> {
            if (CHAT_OPEN) {
                closeChat();
            } else {
                openChat();
            }
        });
    }

    /**
     * Открытие чата с анимацией и инициализацией компонентов.
     */
    public void openChat() {
        hasNewMessages.set(false);
        CHAT_OPEN = true;

        // Сначала устанавливаем нулевую ширину и прозрачность
        SP_CHAT.setPrefWidth(0);
        SP_CHAT.setOpacity(0.0);
        SP_CHAT.getChildren().add(sideChat.getChatVBox());

        // Параллельная анимация появления
        ParallelTransition openAnimation = new ParallelTransition();

        Timeline widthAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(SP_CHAT.prefWidthProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(SP_CHAT.prefWidthProperty(), CHAT_WIDTH, Interpolator.EASE_BOTH))
        );

        Timeline fadeAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(SP_CHAT.opacityProperty(), 0.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(SP_CHAT.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
        );

        openAnimation.getChildren().addAll(widthAnimation, fadeAnimation);
        openAnimation.play();

        processActiveDialog(); // Остальная логика открытия чата
    }

    /**
     * Обработка активного диалога (загрузка сообщений, обновление статусов).
     */
    private void processActiveDialog() {
        DialogController dialogController = sideChat.getDialogController();
        if (dialogController != null && sideChat.getMainChatPane().getChildren().contains(sideChat.getChatDialog())) {
            DialogListView currentDialog = dialogController.getDialogListView();
            if (currentDialog != null && !ServerMessageHandler.isChatRoomPaneOnTop()) {
                currentDialog.refresh();
                openOneRoom(currentDialog);

                Room room = currentDialog.getRoom();
                User secondUser = getSecondUserInOneToOneChat(room);

                // Обработка неотправленных сообщений в отдельном потоке
                messageProcessingExecutor.submit(() -> {
                    List<Message> undeliveredMessages = CH_MESSAGES.findUndeliveredMessagesByRoomAndSecondUser(
                            room.getId(), secondUser.getId());

                    for (Message message : undeliveredMessages) {
                        message.setStatus(MessageStatus.DELIVERED);
                        CH_MESSAGES.update(message);
                        ServiceMessaging.sendNotificationMessageDelivered(message);
                    }
                });
            }
        }
    }

    /**
     * Закрытие чата с анимацией и очисткой ресурсов.
     */
    public void closeChat() {
        // Создаем параллельную анимацию для ширины и прозрачности
        ParallelTransition parallelTransition = new ParallelTransition();

        // Анимация уменьшения ширины с интерполяцией
        Timeline widthAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(SP_CHAT.prefWidthProperty(), SP_CHAT.getWidth(), Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(SP_CHAT.prefWidthProperty(), 0, Interpolator.EASE_BOTH))
        );

        // Анимация исчезновения с интерполяцией
        Timeline fadeAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(SP_CHAT.opacityProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(SP_CHAT.opacityProperty(), 0.0, Interpolator.EASE_BOTH))
        );

        parallelTransition.getChildren().addAll(widthAnimation, fadeAnimation);

        parallelTransition.setOnFinished(e -> {
            Platform.runLater(() -> {
                SP_CHAT.getChildren().clear();
                SP_CHAT.setOpacity(1.0); // Восстанавливаем прозрачность
                closeAllRooms();
                CHAT_OPEN = false;
            });
        });

        parallelTransition.play();
    }
    /**
     * Остановка всех фоновых процессов чата.
     */
    public void shutdown() {
        messageProcessingExecutor.shutdownNow();
        blinkTimeline.stop();
    }

    /* Геттеры и сеттеры */

    public boolean isChatOpen() {
        return CHAT_OPEN;
    }

    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages.set(hasNewMessages);
    }

    public BooleanProperty hasNewMessagesProperty() {
        return hasNewMessages;
    }
}
