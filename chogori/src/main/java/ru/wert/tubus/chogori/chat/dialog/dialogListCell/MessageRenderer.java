package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.cards.*;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.chogori.images.ImageUtil;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;

@Slf4j
public class MessageRenderer {

    private Label lblTitle; // Метка для отображения заголовка сообщения

    public MessageRenderer(Label lblTitle) {
        this.lblTitle = lblTitle;
    }


    /**
     * Отображает сепаратор с датой.
     *
     * @param message Сообщение для отображения.
     * @return Родительский элемент сепаратора.
     */
    public static Parent mountSeparator(Message message) {
        Parent dateSeparator = null;
        try {
            FXMLLoader loader = new FXMLLoader(MessageRenderer.class.getResource("/chogori-fxml/chat/cards/dateSeparator.fxml"));
            dateSeparator = loader.load();
            Label lblDate = (Label) dateSeparator.lookup("#lblDate");
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
    public void mountText(VBox vbMessage, Message message) {
        Label text = new Label(message.getText());
        text.setMaxWidth(CHAT_WIDTH * DialogController.MESSAGE_WIDTH);
        text.setWrapText(true);
        vbMessage.getChildren().add(text);
//        log.debug("Текстовое сообщение отображено: {}", message.getText());
    }

    /**
     * Отображает сообщение с изображениями.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    public void mountPics(VBox vbMessage, Message message) {
        String text = message.getText();
        List<Long> ids = Arrays.asList(text.split(" ", -1))
                .stream().map(Long::valueOf).collect(Collectors.toList());
        for (Long id : ids) {
            Pic p = ChogoriServices.CH_PICS.findById(id);
            String tempFileName = "chat" + "-" + p.getId() + "." + p.getExtension();
            boolean res = ChogoriServices.CH_FILES.download("pics", String.valueOf(p.getId()),
                    "." + p.getExtension(), WinformStatic.WF_TEMPDIR.toString(), "chat", null);

            File file = new File(WinformStatic.WF_TEMPDIR.toString() + "\\" + tempFileName);
            ImageView imageView = ImageUtil.createImageViewFromFile(file, null,
                    (int) CHAT_WIDTH, DialogController.PORTRAIT_WIDTH, DialogController.LANDSCAPE_WIDTH, DialogController.SQUARE_WIDTH);

            Parent cardWithImage = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/card.fxml"));
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
    public void mountDrafts(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithDraft = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/draftCard.fxml"));
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
//            log.debug("Чертеж отображен: {}", id);
        }
    }

    /**
     * Отображает сообщение с комплектами чертежей.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    public void mountFolders(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithFolder = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/folderCard.fxml"));
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
//            log.debug("Комплект чертежей отображен: {}", id);
        }
    }

    /**
     * Отображает сообщение с паспортами.
     *
     * @param vbMessage Контейнер для сообщения.
     * @param message   Сообщение для отображения.
     */
    public void mountPassports(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids = Arrays.asList(text.split(" ", -1));

        for (String id : ids) {
            Parent cardWithPassport = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cards/passportCard.fxml"));
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
//            log.debug("Паспорт отображен: {}", id);
        }
    }
}
