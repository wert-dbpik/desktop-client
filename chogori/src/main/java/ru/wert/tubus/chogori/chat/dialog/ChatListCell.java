package ru.wert.tubus.chogori.chat.dialog;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.cards.*;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.chogori.images.ImageUtil;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.client.entity.models.Room;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_TEMPDIR;

/**
 * Класс ChatListCell отвечает за отображение сообщений в списке чата.
 * Каждое сообщение форматируется в зависимости от его типа (текст, чертежи, паспорта и т.д.).
 */
@Slf4j
public class ChatListCell extends ListCell<Message> {

    private final Boolean ONE_TO_ONE_CHAT; //Индивидуальный чат, не групповой

    public ChatListCell(Room room) {
        ONE_TO_ONE_CHAT = room.getName().startsWith("one-to-one");
    }


    VBox vbMessageContainer; // Самый верхний контейнер для сообщения
    VBox vbOutlineMessage;   // Контейнер, включающий заголовок, сообщение и время создания
    VBox vbMessage;          // Контейнер для самого сообщения
    Label lblFrom;           // Метка для отображения имени отправителя
    Label lblDate;           // Метка для отображения даты сообщения
    Label lblTitle;          // Метка для отображения заголовка сообщения
    Label lblTime;           // Метка для отображения времени сообщения

    static final String OUT = "message_out"; // Стиль для исходящих сообщений
    static final String IN = "message_in";   // Стиль для входящих сообщений

    private Separator separator; // Разделитель между сообщениями

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            Parent mes;
            if(message.getType().equals(Message.MessageType.CHAT_SEPARATOR)){
                mes = mountSeparator(message);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/message.fxml"));
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
                if(ONE_TO_ONE_CHAT){
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
                        mountText(vbMessage, message);
                        break;
                    case CHAT_DRAFTS:
                        mountDrafts(vbMessage, message);
                        break;
                    case CHAT_FOLDERS:
                        mountFolders(vbMessage, message);
                        break;
                    case CHAT_PICS:
                        mountPics(vbMessage, message);
                        break;
                    case CHAT_PASSPORTS:
                        mountPassports(vbMessage, message);
                        break;
                }
            });

        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML для сообщения: {}", e.getMessage(), e);
            return null;
        }
        return inMessage;
    }

    /**
     * Отображает сепаратора с датой.
     *
     * @param message   Сообщение для отображения.
     */
    private Parent mountSeparator(Message message) {
        Parent dateSeparator = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/dateSeparator.fxml"));
            dateSeparator = loader.load();
            Label lblDate = (Label)dateSeparator.lookup("#lblDate");
            lblDate.setStyle("-fx-text-fill: #6f6f71");
            lblDate.setText(message.getText());
        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML для изображения: {}", e.getMessage(), e);
        }
        return dateSeparator;
    }

    /**
     * Отображает текстовое сообщение.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    private void mountText(VBox vbMessage, Message message) {
        vbOutlineMessage.getChildren().removeAll(lblTitle);
        Label text = new Label(message.getText());
        text.setMaxWidth(CHAT_WIDTH * DialogController.MESSAGE_WIDTH);
        text.setWrapText(true);
        vbMessage.getChildren().add(text);
        log.debug("Текстовое сообщение отображено: {}", message.getText());
    }

    /**
     * Отображает сообщение с изображениями.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    private void mountPics(VBox vbMessage, Message message) {
        String text = message.getText();
        List<Long> ids = Arrays.asList(text.split(" ", -1))
                .stream().map(Long::valueOf).collect(Collectors.toList());
        for (Long id : ids) {
            Pic p = ChogoriServices.CH_PICS.findById(id);
            String tempFileName = "chat" + "-" + p.getId() + "." + p.getExtension();
            boolean res = ChogoriServices.CH_FILES.download("pics", String.valueOf(p.getId()),
                    "." + p.getExtension(), WF_TEMPDIR.toString(), "chat", null);

            File file = new File(WF_TEMPDIR.toString() + "\\" + tempFileName);
            ImageView imageView = ImageUtil.createImageViewFromFile(file, null,
                    (int) CHAT_WIDTH, DialogController.PORTRAIT_WIDTH, DialogController.LANDSCAPE_WIDTH, DialogController.SQUARE_WIDTH);

            Parent cardWithImage = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/card.fxml"));
                cardWithImage = loader.load();
                CardController controller = loader.getController();
                controller.init(p.getInitName(), imageView);
            } catch (IOException e) {
                log.error("Ошибка при загрузке FXML для изображения: {}", e.getMessage(), e);
            }

            String title = "Рисунок";
            if (ids.size() > 1) title = "Рисунки";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithImage);
            imageView.fitWidthProperty().unbind();
            log.debug("Изображение отображено: {}", p.getInitName());
        }
    }

    /**
     * Отображает сообщение с чертежами.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    private void mountDrafts(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithDraft = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/draftCard.fxml"));
                cardWithDraft = loader.load();
                DraftCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                log.error("Ошибка при загрузке FXML для чертежа: {}", e.getMessage(), e);
            }

            String title = "Чертеж:";
            if (ids.size() > 1) title = "Чертежи:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithDraft);
            vbMessage.setPrefWidth(CHAT_WIDTH * DialogController.MESSAGE_WIDTH);
            log.debug("Чертеж отображен: {}", id);
        }
    }

    /**
     * Отображает сообщение с комплектами чертежей.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    private void mountFolders(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithFolder = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/folderCard.fxml"));
                cardWithFolder = loader.load();
                FolderCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                log.error("Ошибка при загрузке FXML для комплекта чертежей: {}", e.getMessage(), e);
            }

            String title = "Комплект чертежей:";
            if (ids.size() > 1) title = "Комплекты чертежей:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithFolder);
            vbMessage.setPrefWidth(CHAT_WIDTH * DialogController.MESSAGE_WIDTH);
            log.debug("Комплект чертежей отображен: {}", id);
        }
    }

    /**
     * Отображает сообщение с паспортами.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    private void mountPassports(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithPassport = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/passportCard.fxml"));
                cardWithPassport = loader.load();
                PassportCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                log.error("Ошибка при загрузке FXML для паспорта: {}", e.getMessage(), e);
            }

            String title = "Пасспорт:";
            if (ids.size() > 1) title = "Пасспорта:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithPassport);
            vbMessage.setPrefWidth(CHAT_WIDTH * DialogController.MESSAGE_WIDTH);
            log.debug("Паспорт отображен: {}", id);
        }
    }
}
