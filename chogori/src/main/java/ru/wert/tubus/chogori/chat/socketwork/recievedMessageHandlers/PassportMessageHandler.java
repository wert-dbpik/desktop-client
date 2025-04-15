package ru.wert.tubus.chogori.chat.socketwork.recievedMessageHandlers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.serviceQUICK.DraftQuickService;
import ru.wert.tubus.client.entity.serviceQUICK.PassportQuickService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.tubus.client.entity.serviceQUICK.DraftQuickService.LOADED_DRAFTS;
import static ru.wert.tubus.client.entity.serviceQUICK.PassportQuickService.LOADED_PASSPORTS;

/**
 * Обработчик сообщений о чертежах.
 */
@Slf4j
public class PassportMessageHandler {
    public static void handle(Message message, Message.MessageType type, StringBuilder str) {
        Gson gson = GsonConfiguration.createGson();
        Passport passport = gson.fromJson(message.getText(), Passport.class);

        switch (type) {
            case UPDATE_PASSPORT:
                processUpdatePassport(passport, str);
                break;

        }
    }

    private static void processUpdatePassport(Passport passport, StringBuilder str) {
        str.append("Пользователь ");
        str.append("обновил пасспорт: ");
        str.append(passport.toUsefulString());

        // Получаем текущую версию паспорта из кеша (до обновления)
        Passport foundPassport = null;
        for(Passport p : LOADED_PASSPORTS) {
            if(p.getId().equals(passport.getId())) {
                foundPassport = p;
                break;
            }
        }

        // Обновляем паспорт в кеше
        if(foundPassport != null) {
            LOADED_PASSPORTS.remove(foundPassport);
        }
        LOADED_PASSPORTS.add(passport);

        DraftQuickService.reload();

        // Обновляем все открытые вкладки редактора чертежей
        for(Tab tab: CH_TAB_PANE.getTabs()) {
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController) {
                Platform.runLater(()->{
                    ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
                });

            }
        }
    }

}
