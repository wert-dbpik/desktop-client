package ru.wert.tubus.chogori.chat.dialog;

import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import lombok.Getter;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.chogori.images.ImageUtil;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Pic;
import ru.wert.tubus.client.entity.models.Room;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * ListViewDialog отличается от ListView только полем Room, по которому происходит его идентификация
 */
public class ListViewDialog extends ListView<Message> {

    @Getter
    private final Room room;
    private TextArea taMessageText;

    public ListViewDialog(Room room, TextArea taMessageText) {
        this.room = room;
        this.taMessageText = taMessageText;
    }

    //============          ОТПРАВИТЬ ПАССПОРТА   ========================================================

    /**
     * Метод создает сообщение с пасспортами
     */
    public void createPassportsChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("PP")) continue;
            else {
                String strId = s.replace("PP#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        Message message = createChatMessage(Message.MessageType.CHAT_PASSPORTS, text.toString().trim());
        taMessageText.setText("");

        sendMessageToRecipient(message);

    }

    //============          ОТПРАВИТЬ ЧЕРТЕЖИ   ========================================================

    /**
     * Метод создает сообщение с чертежами
     */
    public void createDraftsChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("DR")) continue;
            else {
                String strId = s.replace("DR#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        Message message = createChatMessage(Message.MessageType.CHAT_DRAFTS, text.toString().trim());
        taMessageText.setText("");

        sendMessageToRecipient(message);

    }

    /**
     * Метод создает сообщение с комплектами чертежей
     */
    public void createFoldersChatMessage(String str) {
        StringBuilder text = new StringBuilder();
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            if (!clazz.equals("F")) continue;
            else {
                String strId = s.replace("F#", "");
                text.append(strId);
                text.append(" ");
            }
        }

        Message message = createChatMessage(Message.MessageType.CHAT_FOLDERS, text.toString().trim());
        taMessageText.setText("");

        sendMessageToRecipient(message);

    }

    //============          ОТПРАВИТЬ ИЗОБРАЖЕНИЯ   ========================================================

    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ ИЗОБРАЖЕНИЯ
     * При нажатии открывается вкладка "Чертежи"
     */
    public void sendPicture(ActionEvent event) {
        StringBuilder text = new StringBuilder();
        // Пользователь выбирает несколько рисунков
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg");
        List<File> chosenFiles = AppStatic.chooseManyFile(event, new File("C:\\"), filter);
        if(chosenFiles == null || chosenFiles.isEmpty()) return;

        createPicsChatMessage(chosenFiles);
    }

    /**
     * Метод создает сообщение с изображениями
     * @param chosenFiles
     */
    public void createPicsChatMessage( List<File> chosenFiles) {
        StringBuilder text = new StringBuilder();
        for(File file : chosenFiles){
            Image image = new Image(file.toURI().toString());
            Pic savedPic = ImageUtil.createPicFromFileAndSaveItToDB(image, file);
            text.append(savedPic.getId());
            text.append(" ");
        }

        Message message = createChatMessage(Message.MessageType.CHAT_PICS, text.toString().trim());
        taMessageText.setText("");

        sendMessageToRecipient(message);

    }

    //============          ОТПРАВИТЬ ТЕКСТ   ========================================================


    /**
     * Обработка нажатия на кнопку ОТПРАВИТЬ
     * Эта кнопка отправляет только текстовые сообщения
     */
    public void sendText() {
        String text = taMessageText.getText();
        if(text == null || text.isEmpty()) return;
        Message message = createChatMessage(Message.MessageType.CHAT_TEXT, text);
        taMessageText.setText("");

        sendMessageToRecipient(message);
    }



    //=====================    ОБЩИЕ МЕТОДЫ    =================================================

    /**
     * Метода создает сообщение Message
     * @param text String
     */
    public Message createChatMessage(Message.MessageType type, String text){
        Message message = new Message();
        message.setType(type);
        message.setRoomId(room.getId());
        message.setSenderId(CH_CURRENT_USER.getId());
        message.setCreationTime(LocalDateTime.now());
        message.setStatus(Message.MessageStatus.RECEIVED);
        message.setText(text);

        return message;
    }

    /**
     * Собственно отправка сообщения пользователю
     * @param message
     */
    private void sendMessageToRecipient(Message message) {
        SocketService.sendMessage(message);
        getItems().add(message);
        refresh();
        scrollTo(message);
    }

    /**
     * Получение сообщения от сервера (пользователю)
     * @param message
     */
    public void receiveMessageFromServer(Message message) {
        getItems().add(message);
        refresh();
        scrollTo(message);
    }
}
