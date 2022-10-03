package ru.wert.datapik.utils.chat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ChatListCell extends ListCell<ChatMessage> {

    VBox vbMessageContainer; //Самый верхний контейнер
    VBox vbMessage; //Само сообщение
    Label lblFrom;
    Label lblDate;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/message.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            separator.setVisible(false);
            lblFrom = (Label) inMessage.lookup("#lblFrom");
            lblDate = (Label) inMessage.lookup("#lblDate");
            vbMessageContainer = (VBox) inMessage.lookup("#vbMessageContainer");
            vbMessage = (VBox) inMessage.lookup("#vbMessage");
            vbMessage.prefWidthProperty().bind(separator.widthProperty().multiply(0.8));

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

                lblFrom.setText(message.getUser().getName());
                lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime()));
                EMessageType type = EMessageType.values()[message.getMessageType()];

                switch(type){
                    case CHAT_TEXT: mountText(vbMessage, message); break;
                    case CHAT_DRAFTS: mountDrafts(vbMessage, message); break;
                    case CHAT_PICS: mountPics(vbMessage, message); break;
                }

                vbMessage.autosize();
            });




        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inMessage;
    }


    private void mountText(VBox vbMessage, ChatMessage message) {
        Label text = new Label(message.getText());
        text.setStyle("-fx-text-fill: black; -fx-font-size: 14;");
//        text.setPrefWidth(vbMessage.getWidth());
        text.prefWidthProperty().bind(separator.widthProperty().multiply(0.9));
        text.setWrapText(true);
//        text.setId("blobTextArea");

        vbMessage.getChildren().add(text);

    }

    private void mountPics(VBox vbMessage, ChatMessage message) {
    }

    private void mountDrafts(VBox vbMessage, ChatMessage message) {
    }






}
