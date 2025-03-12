package ru.wert.tubus.chogori.chat.socketwork;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.entities.users.User_TableView;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceQUICK.UserQuickService;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.SP_NOTIFICATION;

/**
 * Класс обработчик сообщений со стороны сервера
 */
@Slf4j
public class ServerMessageHandler {

    public static void handle(Message message) {
        log.info(String.format("Message from server received: %s", message.toUsefulString()));
        Platform.runLater(() -> {
            if (SP_NOTIFICATION != null && message != null)
                SP_NOTIFICATION.setText(makeString(message));
        });
    }


    private static String makeString(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder str = new StringBuilder("");
        Message.MessageType type = message.getType();

        if (type == Message.MessageType.USER_IN || type == Message.MessageType.USER_OUT) {
            str.append(type.getTypeName()).append(" ").append(CH_USERS.findById(message.getSenderId()).getName());
            UserQuickService.getInstance().fetchUserById(message.getSenderId()).ifPresent(
                    user -> {
                        user.setOnline(type == Message.MessageType.USER_IN);
                        for(Tab tab: CH_TAB_PANE.getTabs()){
                            if(tab.getId().equals("Пользователи"))
                                Platform.runLater(()->{
                                    ((UpdatableTabController) ((AppTab)tab).getTabController()).updateTab();
                                });
                        }
                    }
            );
        } else if (type == Message.MessageType.ADD_DRAFT || type == Message.MessageType.UPDATE_DRAFT || type == Message.MessageType.DELETE_DRAFT) {
            try {
                Draft draft = objectMapper.readValue(message.getText(), Draft.class);
                str.append("Пользователь ");
                str.append(draft.getStatusUser().toUsefulString());
                str.append(type.getTypeName()).append(draft.toUsefulString());
            } catch (JsonProcessingException e) {
                // Обработка ошибки парсинга JSON
                System.err.println("Ошибка при парсинге JSON: " + e.getMessage());
            }
        }

        return str.toString();
    }

}
