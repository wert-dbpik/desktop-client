package ru.wert.datapik.utils.chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ChatListCell extends ListCell<ChatMessage> {

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/inMessage.fxml"));
            inMessage = loader.load();
            separator = (Separator) inMessage.lookup("#separator");
            Label lblFrom = (Label) inMessage.lookup("#lblFrom");
            Label lblDate = (Label) inMessage.lookup("#lblDate");
            VBox vbMessage = (VBox) inMessage.lookup("#vbMessage");
            vbMessage.prefWidthProperty().bind(separator.widthProperty().multiply(0.8));

            lblFrom.setText(message.getUser().getName());
            lblDate.setText(AppStatic.parseStringToDate(message.getCreationTime()));
            EMessageType type = EMessageType.values()[message.getMessageType()];

            switch(type){
                case CHAT_TEXT: mountText(vbMessage, message); break;
                case CHAT_DRAFTS: mountDrafts(vbMessage, message); break;
                case CHAT_PICS: mountPics(vbMessage, message); break;
            }

            if(in_out.equals(OUT)){
                lblFrom.setStyle("-fx-text-alignment: right; -fx-alignment: top-right; -fx-font-style: oblique; -fx-text-fill: royalblue;");
                lblDate.setStyle("-fx-text-alignment: right; -fx-alignment: top-right; -fx-font-style: oblique; -fx-text-fill: royalblue;");
                vbMessage.setStyle("-fx-alignment: top-right; -fx-start-margin: 50px;");
            } else {
                lblFrom.setStyle("-fx-text-alignment: left; -fx-alignment: top-left; -fx-font-style: oblique; -fx-text-fill: royalblue;");
                lblDate.setStyle("-fx-text-alignment: left; -fx-alignment: top-left; -fx-font-style: oblique; -fx-text-fill: royalblue;");
                vbMessage.setStyle("-fx-alignment: top-left; -fx-end-margin: 50px;");
            }

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
