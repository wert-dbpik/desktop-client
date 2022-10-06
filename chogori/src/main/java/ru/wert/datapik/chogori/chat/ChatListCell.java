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
import ru.wert.datapik.client.entity.models.ChatMessage;
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

public class ChatListCell extends ListCell<ChatMessage> {

    VBox vbMessageContainer; //Самый верхний контейнер
    VBox vbMessage; //Само сообщение
    Label lblFrom;
    Label lblDate;
    Label lblTime;
    static final String OUT = "message_out"; //Исходящие сообщения
    static final String IN = "message_in";   //Входящие сообщения

    private Separator separator;

    @Override
    protected void updateItem(ChatMessage item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            Parent mes;
            if(item.getUser().equals(CH_CURRENT_USER))
                mes = formatMessage(item, OUT);
            else
                mes = formatMessage(item, IN);

            setGraphic(mes);
        }
    }

    private VBox formatMessage(ChatMessage message, String in_out){
        VBox inMessage = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/message.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            separator.setVisible(false);
            lblFrom = (Label) inMessage.lookup("#lblFrom");
            lblDate = (Label) inMessage.lookup("#lblDate");
            lblTime = (Label) inMessage.lookup("#lblTime");
            vbMessageContainer = (VBox) inMessage.lookup("#vbMessageContainer");
            vbMessage = (VBox) inMessage.lookup("#vbMessage");


            if(in_out.equals(OUT)){
                vbMessageContainer.setAlignment(Pos.TOP_RIGHT);
                lblFrom.setId("outMessageData");
                lblDate.setId("outMessageData");
                vbMessage.setId("outMessage");
            } else {
                vbMessageContainer.setAlignment(Pos.TOP_LEFT);
                lblFrom.setId("inMessageData");
                lblDate.setId("inMessageData");
                vbMessage.setId("inMessage");
            }

            Platform.runLater(()->{
                vbMessage.autosize();
                lblFrom.setText(message.getUser().getName());
                lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime()));
                lblTime.setText(AppStatic.parseStringToTime(message.getCreationTime()));
                EMessageType type = EMessageType.values()[message.getMessageType()];

                switch(type){
                    case CHAT_TEXT: mountText(vbMessage, message); break;
                    case CHAT_DRAFTS: mountDrafts(vbMessage, message); break;
                    case CHAT_PICS: mountPics(vbMessage, message); break;
                }


            });

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inMessage;
    }


    private void mountText(VBox vbMessage, ChatMessage message) {
        Label text = new Label(message.getText());
        text.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        text.setWrapText(true);
        vbMessage.getChildren().add(text);
    }

    private void mountPics(VBox vbMessage, ChatMessage message) {
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/card.fxml"));
                cardWithImage = loader.load();
                CardController controller = loader.getController();
                controller.init("Изображение", imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }

            vbMessage.getChildren().add(cardWithImage);
            imageView.fitWidthProperty().unbind();
        }


    }

    private void mountDrafts(VBox vbMessage, ChatMessage message) {
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

            vbMessage.getChildren().add(cardWithDraft);
            vbMessage.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        }


    }






}
