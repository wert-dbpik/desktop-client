package ru.wert.datapik.chogori.chat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.chat.cards.CardController;
import ru.wert.datapik.chogori.chat.cards.DraftCardController;
import ru.wert.datapik.chogori.chat.cards.FolderCardController;
import ru.wert.datapik.chogori.chat.cards.PassportCardController;
import ru.wert.datapik.client.entity.models.Message;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.chogori.images.ImageUtil;
import ru.wert.datapik.chogori.statics.AppStatic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.datapik.chogori.chat.SideChatTalkController.*;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FILES;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PICS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_TEMPDIR;

public class ChatListCell extends ListCell<Message> {

    VBox vbMessageContainer; //Самый верхний контейнер
    VBox vbOutlineMessage; //Контейнер включает Заголовок, Сообщение, Время создания
    VBox vbMessage; //Само сообщение
    Label lblFrom;
    Label lblDate;
    Label lblTitle;
    Label lblTime;
    static final String OUT = "message_out"; //Исходящие сообщения
    static final String IN = "message_in";   //Входящие сообщения

    private Separator separator;

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            Parent mes;
            if(item.getSender().equals(CH_CURRENT_USER))
                mes = formatMessage(item, OUT);
            else
                mes = formatMessage(item, IN);

            setGraphic(mes);
        }
    }

    private VBox formatMessage(Message message, String in_out){
        VBox inMessage = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/message.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            separator.setVisible(false);
            lblFrom = (Label) inMessage.lookup("#lblFrom");
            lblDate = (Label) inMessage.lookup("#lblDate");
            lblTitle = (Label) inMessage.lookup("#lblTitle");
            lblTime = (Label) inMessage.lookup("#lblTime");
            lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime()));
            vbMessageContainer = (VBox) inMessage.lookup("#vbMessageContainer");
            vbOutlineMessage = (VBox) inMessage.lookup("#vbOutlineMessage");
            vbMessage = (VBox) inMessage.lookup("#vbMessage");

            lblTitle.setId("messageTitleLabel");
            lblTime.setId("messageTimeLabel");
            if(in_out.equals(OUT)){
                vbMessageContainer.getChildren().removeAll(lblFrom);
                vbMessageContainer.getChildren().removeAll(lblDate);
                vbMessageContainer.setAlignment(Pos.TOP_RIGHT);
                lblFrom.setId("outMessageDataLabel");
                lblDate.setId("outMessageDataLabel");
                vbOutlineMessage.setId("outOutlineMessageVBox");
                vbMessage.setId("outMessageVBox");
            } else {
                vbMessageContainer.setAlignment(Pos.TOP_LEFT);
                lblFrom.setId("inMessageDataLabel");
                lblDate.setId("inMessageDataLabel");
                vbOutlineMessage.setId("inOutlineMessageVBox");
                vbMessage.setId("inMessageVBox");
            }

            Platform.runLater(()->{
                vbMessage.autosize();
                lblFrom.setText(message.getSender().getName());
                lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime()));

//                Message.MessageType type = message.getType();

                switch(message.getType()){
                    case CHAT_TEXT: mountText(vbMessage, message); break;
                    case CHAT_DRAFTS: mountDrafts(vbMessage, message); break;
                    case CHAT_FOLDERS: mountFolders(vbMessage, message); break;
                    case CHAT_PICS: mountPics(vbMessage, message); break;
                    case CHAT_PASSPORTS: mountPassports(vbMessage, message); break;
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inMessage;
    }


    private void mountText(VBox vbMessage, Message message) {
        vbOutlineMessage.getChildren().removeAll(lblTitle);
        Label text = new Label(message.getText());
        text.setMaxWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        text.setWrapText(true);
        vbMessage.getChildren().add(text);
    }

    private void mountPics(VBox vbMessage, Message message) {
        String text = message.getText();
        List<Long> ids =  Arrays.asList(text.split(" ", -1))
                .stream().map(Long::valueOf).collect(Collectors.toList());
        for(Long id : ids){
            Pic p = CH_PICS.findById(id);
            String tempFileName = "chat" + "-" + p.getId() + "." + p.getExtension();
            boolean res = CH_FILES.download("pics", //Постоянная папка в каталоге для чертежей
                    String.valueOf(p.getId()), //название скачиваемого файла
                    "." + p.getExtension(), //расширение скачиваемого файла
                    WF_TEMPDIR.toString(),//временная папка, куда необходимо скачать фай
                    "chat"); //префикс

            File file = new File(WF_TEMPDIR.toString() + "\\" + tempFileName);
            //Добавляем файл в общий список
            ImageView imageView = ImageUtil.createImageViewFromFile(file, null,
                    (int) CHAT_WIDTH, PORTRAIT_WIDTH, LANDSCAPE_WIDTH, SQUARE_WIDTH);

            Parent cardWithImage = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/cardd.fxml"));
                cardWithImage = loader.load();
                CardController controller = loader.getController();
                controller.init(p.getInitName(), imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = "Рисунок";
            if(ids.size() > 1) title = "Рисунки";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithImage);
            imageView.fitWidthProperty().unbind();
        }


    }

    private void mountDrafts(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids =  Arrays.asList(text.split(" ", -1));

        for(String id : ids){
            Parent cardWithDraft = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/draftCard.fxml"));
                cardWithDraft = loader.load();
                DraftCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = "Чертеж:";
            if(ids.size() > 1) title = "Чертежи:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithDraft);
            vbMessage.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        }


    }

    private void mountFolders(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids =  Arrays.asList(text.split(" ", -1));

        for(String id : ids){
            Parent cardWithFolder = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/folderCard.fxml"));
                cardWithFolder = loader.load();
                FolderCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = "Комплект чертежей:";
            if(ids.size() > 1) title = "Комплекты чертежей:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithFolder);
            vbMessage.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        }


    }

    private void mountPassports(VBox vbMessage, Message message) {
        String text = message.getText();
        List<String> ids =  Arrays.asList(text.split(" ", -1));

        for(String id : ids){
            Parent cardWithPassport = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/passportCard.fxml"));
                cardWithPassport = loader.load();
                PassportCardController controller = loader.getController();
                controller.init(id);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = "Пасспорт:";
            if(ids.size() > 1) title = "Пасспорта:";
            lblTitle.setText(title);

            vbMessage.getChildren().add(cardWithPassport);
            vbMessage.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        }


    }






}
