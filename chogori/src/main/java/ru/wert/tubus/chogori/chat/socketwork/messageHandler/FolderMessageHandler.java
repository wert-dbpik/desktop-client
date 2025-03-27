package ru.wert.tubus.chogori.chat.socketwork.messageHandler;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

/**
 * Обработчик сообщений о папках.
 */
@Slf4j
public class FolderMessageHandler {
    public static void handle(Message message, Message.MessageType type, StringBuilder str) {
        Gson gson = GsonConfiguration.createGson();
        Folder folder = gson.fromJson(message.getText(), Folder.class);

        switch (type) {
            case ADD_FOLDER:
                processAddFolder(folder, str);
                break;
            case UPDATE_FOLDER:
                processUpdateFolder(folder, str);
                break;
            case DELETE_FOLDER:
                processDeleteFolder(folder, str);
                break;
        }
    }

    private static void processAddFolder(Folder folder, StringBuilder str) {
        str.append("Пользователь добавил комплект чертежей: ");
        str.append(folder.toUsefulString());
    }

    private static void processUpdateFolder(Folder folder, StringBuilder str) {
        str.append("Пользователь изменил комплект чертежей: ");
        str.append(folder.toUsefulString());
    }

    private static void processDeleteFolder(Folder folder, StringBuilder str) {
        str.append("Пользователь удалил комплект чертежей: ");
        str.append(folder.toUsefulString());
    }
}
