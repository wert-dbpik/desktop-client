package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.utils.MessageStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell.IN;
import static ru.wert.tubus.chogori.chat.dialog.dialogListCell.DialogListCell.OUT;
import static ru.wert.tubus.chogori.images.BtnImages.CHAT_DELIVERED_IMG;

@Slf4j
public class MessageCardsManager {
    private final VBox root; // Корневой элемент сообщения
    private final ImageView statusIcon; // Иконка статуса сообщения
    private final Label timeLabel; // Метка времени
    private final Label fromLabel; // Метка отправителя
    private final Label dateLabel; // Метка даты
    private final Label titleLabel; // Метка заголовка
    private final VBox messageContainer; // Контейнер содержимого сообщения
    private final VBox outlineContainer; // Контейнер с рамкой
    private final HBox statusContainer; // Контейнер статуса
    private final Separator separator; // Разделитель сообщений

    private Message currentMessage; // Текущее отображаемое сообщение
    private final boolean isOneToOneChat; // Флаг личного чата

    /**
     * Конструктор, загружающий FXML и настраивающий базовые параметры
     * @param room Комната чата, определяющая стиль отображения
     */
    public MessageCardsManager(Room room) {
        try {
            // Загрузка FXML шаблона
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/message.fxml"));
            root = loader.load();

            // Инициализация элементов интерфейса
            statusIcon = (ImageView) root.lookup("#imgStatus");
            timeLabel = (Label) root.lookup("#lblTime");
            fromLabel = (Label) root.lookup("#lblFrom");
            dateLabel = (Label) root.lookup("#lblDate");
            titleLabel = (Label) root.lookup("#lblTitle");
            messageContainer = (VBox) root.lookup("#vbMessage");
            outlineContainer = (VBox) root.lookup("#vbOutlineMessage");
            statusContainer = (HBox) root.lookup("#hbStatus");
            separator = (Separator) root.lookup("#separator");

            separator.setVisible(false);
            isOneToOneChat = room.getName().startsWith("one-to-one");

            // Базовая настройка стилей
            configureBaseStyles();
        } catch (IOException e) {
            log.error("Ошибка загрузки FXML шаблона сообщения", e);
            throw new RuntimeException("Не удалось загрузить шаблон сообщения", e);
        }
    }

    /**
     * Привязывает сообщение к UI элементам
     * @param message Сообщение для отображения
     * @param direction Направление сообщения (IN/OUT)
     */
    public void bindMessage(Message message, String direction) {
        unbindCurrentMessage();
        currentMessage = message;

        // Настройка стилей в зависимости от направления
        configureDirectionStyles(direction);

        // Биндинг свойств сообщения к UI элементам
        bindProperties(message, direction);

        // Отображение содержимого сообщения
        renderMessageContent(message);

        // Слушатель изменения статуса
        message.statusProperty().addListener((obs, oldVal, newVal) ->
                updateMessageStatus(newVal)
        );
    }

    /**
     * Отвязывает текущее сообщение от UI элементов
     */
    public void unbindCurrentMessage() {
        if (currentMessage != null) {
            statusIcon.imageProperty().unbind();
            timeLabel.textProperty().unbind();
            fromLabel.textProperty().unbind();
            dateLabel.textProperty().unbind();
        }
    }

    /**
     * Возвращает корневой элемент для отображения
     */
    public Node getView() {
        return root;
    }

    // Приватные вспомогательные методы

    private void configureBaseStyles() {
        titleLabel.setId("messageTitleLabel");
        timeLabel.setId("messageTimeLabel");
    }

    private void configureDirectionStyles(String direction) {
        if (direction.equals(OUT)) {
            root.getChildren().removeAll(fromLabel, dateLabel);
            root.setAlignment(Pos.TOP_RIGHT);
            outlineContainer.setId("outOutlineMessageVBox");
            messageContainer.setId("outMessageVBox");
        } else {
            if (isOneToOneChat) {
                root.getChildren().removeAll(fromLabel, dateLabel);
            }
            root.setAlignment(Pos.TOP_LEFT);
            fromLabel.setId("inMessageDataLabel");
            dateLabel.setId("inMessageDataLabel");
            outlineContainer.setId("inOutlineMessageVBox");
            messageContainer.setId("inMessageVBox");
        }
    }

    private void bindProperties(Message message, String direction) {
        Platform.runLater(() -> {
            // Биндинг времени
            timeLabel.textProperty().bind(
                    Bindings.createStringBinding(() ->
                                    formatTime(message.getCreationTime()),
                            message.creationTimeProperty()
                    )
            );

            // Биндинг статуса (только для исходящих сообщений)
            if (direction.equals(OUT)) {
                statusIcon.visibleProperty().bind(
                        message.statusProperty().isEqualTo(MessageStatus.DELIVERED)
                );
                statusIcon.imageProperty().set(CHAT_DELIVERED_IMG);
            } else {
                statusContainer.getChildren().remove(statusIcon);
            }

            // Биндинг отправителя и даты
            fromLabel.setText(CH_USERS.findById(message.getSenderId()).getName());
            dateLabel.setText(formatDate(message.getCreationTime()));
        });
    }

    private void renderMessageContent(Message message) {
        MessageCardsRenderer renderer = new MessageCardsRenderer(titleLabel);

        // Очистка предыдущего содержимого
        messageContainer.getChildren().clear();
        outlineContainer.getChildren().remove(titleLabel);

        // Рендеринг в зависимости от типа сообщения
        switch (message.getType()) {
            case CHAT_TEXT:
                renderer.mountText(messageContainer, message);
                break;
            case CHAT_DRAFTS:
                renderer.mountDrafts(messageContainer, message);
                break;
            case CHAT_FOLDERS:
                renderer.mountFolders(messageContainer, message);
                break;
            case CHAT_PICS:
                renderer.mountPics(messageContainer, message);
                break;
            case CHAT_PASSPORTS:
                renderer.mountPassports(messageContainer, message);
                break;
        }
    }

    void updateMessageStatus(MessageStatus status) {
        Platform.runLater(() -> {
            if (statusIcon == null) return;

            // Удаляем существующий binding
            statusIcon.visibleProperty().unbind();

            switch (status) {
                case DELIVERED:
                    statusIcon.setImage(CHAT_DELIVERED_IMG);
                    statusIcon.setVisible(true);
                    break;
                default:
                    statusIcon.setVisible(false);
            }

            statusContainer.requestLayout();
        });
    }

    private String formatTime(LocalDateTime time) {
        return time != null ? AppStatic.parseStringToTime(time.toString()) : "";
    }

    private String formatDate(LocalDateTime time) {
        return time != null ? AppStatic.parseStringToDate(time.toString()) : "";
    }
}
