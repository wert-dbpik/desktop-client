package ru.wert.tubus.chogori.chat.socketwork.messageHandlers;

import com.google.gson.Gson;
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

/**
 * Обработчик сообщений о чертежах.
 */
@Slf4j
public class DraftMessageHandler {
    public static void handle(Message message, Message.MessageType type, StringBuilder str) {
        Gson gson = GsonConfiguration.createGson();
        Draft draft = gson.fromJson(message.getText(), Draft.class);

        switch (type) {
            case ADD_DRAFT:
                processAddDraft(draft, str);
                break;
            case UPDATE_DRAFT:
                processUpdateDraft(draft, str);
                break;
            case DELETE_DRAFT:
                processDeleteDraft(draft, str);
                break;
        }
    }

    private static void processAddDraft(Draft draft, StringBuilder str) {
        str.append("Пользователь ");
        str.append(draft.getStatusUser().toUsefulString());
        str.append(" добавил чертеж: ");
        str.append(draft.getPassport().toUsefulString());

        Passport passport = draft.getPassport();
        if(!PassportQuickService.LOADED_PASSPORTS.contains(passport))
            PassportQuickService.LOADED_PASSPORTS.add(passport);

        if(!DraftQuickService.LOADED_DRAFTS.contains(draft))
            DraftQuickService.LOADED_DRAFTS.add(draft);

        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController){
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }

    private static void processUpdateDraft(Draft draft, StringBuilder str) {
        str.append("Пользователь ");
        str.append(draft.getStatusUser().toUsefulString());
        str.append(" обновил чертеж: ");
        str.append(draft.getPassport().toUsefulString());

        Passport passport = draft.getPassport();
        // Обновляем паспорт в кеше, если он там есть
        if(PassportQuickService.LOADED_PASSPORTS.contains(passport)) {
            PassportQuickService.LOADED_PASSPORTS.remove(passport);
            PassportQuickService.LOADED_PASSPORTS.add(passport);
        }

        // Обновляем чертеж в кеше
        if(DraftQuickService.LOADED_DRAFTS.contains(draft)) {
            DraftQuickService.LOADED_DRAFTS.remove(draft);
            DraftQuickService.LOADED_DRAFTS.add(draft);
        } else {
            DraftQuickService.LOADED_DRAFTS.add(draft);
        }

        // Обновляем все открытые вкладки редактора чертежей
        for(Tab tab: CH_TAB_PANE.getTabs()) {
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController) {
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }

    private static void processDeleteDraft(Draft draft, StringBuilder str) {
        str.append("Пользователь ");
        str.append(draft.getStatusUser().toUsefulString());
        str.append(" удалил чертеж: ");
        str.append(draft.getPassport().toUsefulString());

        // Удаляем чертеж из кеша
        DraftQuickService.LOADED_DRAFTS.remove(draft);

        // Обновляем все открытые вкладки редактора чертежей
        for(Tab tab: CH_TAB_PANE.getTabs()) {
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController) {
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }
}
