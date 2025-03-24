package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import java.io.IOException;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;

@Slf4j
public class DialogListCell extends ListCell<Message> {

    private final Boolean ONE_TO_ONE_CHAT; // Индивидуальный чат, не групповой

    private VBox vbMessageContainer; // Самый верхний контейнер для сообщения
    private VBox vbOutlineMessage;   // Контейнер, включающий заголовок, сообщение и время создания
    private VBox vbMessage;          // Контейнер для самого сообщения
    private Label lblFrom;           // Метка для отображения имени отправителя
    private Label lblDate;           // Метка для отображения даты сообщения
    private Label lblTitle;          // Метка для отображения заголовка сообщения
    private Label lblTime;           // Метка для отображения времени сообщения

    static final String OUT = "message_out"; // Стиль для исходящих сообщений
    static final String IN = "message_in";   // Стиль для входящих сообщений

    private Separator separator; // Разделитель между сообщениями

    public DialogListCell(Room room) {
        ONE_TO_ONE_CHAT = room.getName().startsWith("one-to-one");
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            Parent mes;
            if (message.getType().equals(Message.MessageType.CHAT_SEPARATOR)) {
                mes = MessageRenderer.mountSeparator(message);
            } else {
                // Определяем, является ли сообщение исходящим или входящим
                if (message.getSenderId() != null && message.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId())) {
                    mes = formatMessage(message, OUT);
                } else {
                    mes = formatMessage(message, IN);
                }
            }

            setGraphic(mes);
        }
    }

    /**
     * Форматирует сообщение в зависимости от его типа (входящее или исходящее).
     *
     * @param message Сообщение для форматирования.
     * @param in_out  Тип сообщения (входящее или исходящее).
     * @return Отформатированное сообщение в виде VBox.
     */
    private VBox formatMessage(Message message, String in_out) {
        VBox inMessage = null;
        try {
            // Загружаем шаблон сообщения из FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/message.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            separator.setVisible(false); // Скрываем разделитель

            // Инициализируем элементы интерфейса
            lblFrom = (Label) inMessage.lookup("#lblFrom");
            lblDate = (Label) inMessage.lookup("#lblDate");
            lblTitle = (Label) inMessage.lookup("#lblTitle");
            lblTime = (Label) inMessage.lookup("#lblTime");
            lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime().toString()));

            vbMessageContainer = (VBox) inMessage.lookup("#vbMessageContainer");
            vbOutlineMessage = (VBox) inMessage.lookup("#vbOutlineMessage");
            vbMessage = (VBox) inMessage.lookup("#vbMessage");

            lblTitle.setId("messageTitleLabel");
            lblTime.setId("messageTimeLabel");

            MessageRenderer messageRenderer = new MessageRenderer(lblTitle);

            // Настройка стилей для исходящих и входящих сообщений
            if (in_out.equals(OUT)) {
                vbMessageContainer.getChildren().removeAll(lblFrom);
                vbMessageContainer.getChildren().removeAll(lblDate);
                vbMessageContainer.setAlignment(Pos.TOP_LEFT);
                lblFrom.setId("outMessageDataLabel");
                lblDate.setId("outMessageDataLabel");
                vbOutlineMessage.setId("outOutlineMessageVBox");
                vbMessage.setId("outMessageVBox");
            } else {
                if (ONE_TO_ONE_CHAT) {
                    vbMessageContainer.getChildren().removeAll(lblFrom);
                    vbMessageContainer.getChildren().removeAll(lblDate);
                }
                vbMessageContainer.setAlignment(Pos.TOP_RIGHT);
                lblFrom.setId("inMessageDataLabel");
                lblDate.setId("inMessageDataLabel");
                vbOutlineMessage.setId("inOutlineMessageVBox");
                vbMessage.setId("inMessageVBox");
            }

            // Обновляем интерфейс в потоке JavaFX
            Platform.runLater(() -> {
                vbMessage.autosize();

                lblFrom.setText(CH_USERS.findById(message.getSenderId()).getName());
                lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime().toString()));

                // В зависимости от типа сообщения вызываем соответствующий метод для отображения
                switch (message.getType()) {
                    case CHAT_TEXT:
                        vbOutlineMessage.getChildren().removeAll(lblTitle);
                        messageRenderer.mountText(vbMessage, message);
                        break;
                    case CHAT_DRAFTS:
                        messageRenderer.mountDrafts(vbMessage, message);
                        break;
                    case CHAT_FOLDERS:
                        messageRenderer.mountFolders(vbMessage, message);
                        break;
                    case CHAT_PICS:
                        messageRenderer.mountPics(vbMessage, message);
                        break;
                    case CHAT_PASSPORTS:
                        messageRenderer.mountPassports(vbMessage, message);
                        break;
                }
            });

        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML для сообщения: {}", e.getMessage(), e);
            return null;
        }
        return inMessage;
    }


}
