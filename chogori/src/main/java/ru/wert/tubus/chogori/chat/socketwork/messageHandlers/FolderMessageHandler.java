package ru.wert.tubus.chogori.chat.socketwork.messageHandlers;

import com.google.gson.Gson;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.serviceQUICK.DraftQuickService;
import ru.wert.tubus.client.entity.serviceQUICK.FolderQuickService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

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

        if(!FolderQuickService.LOADED_FOLDERS.contains(folder))
            FolderQuickService.LOADED_FOLDERS.add(folder);

        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController){
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }

    private static void processUpdateFolder(Folder folder, StringBuilder str) {
        str.append("Пользователь изменил комплект чертежей: ");
        str.append(folder.toUsefulString());

        // Обновляем чертеж в кеше
        if(FolderQuickService.LOADED_FOLDERS.contains(folder)) {
            FolderQuickService.LOADED_FOLDERS.remove(folder);
            FolderQuickService.LOADED_FOLDERS.add(folder);
        } else {
            FolderQuickService.LOADED_FOLDERS.add(folder);
        }

        // Обновляем все открытые вкладки редактора чертежей
        for(Tab tab: CH_TAB_PANE.getTabs()) {
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController) {
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }

    private static void processDeleteFolder(Folder folder, StringBuilder str) {
        str.append("Пользователь удалил комплект чертежей: ");
        str.append(folder.toUsefulString());

        // Удаляем чертеж из кеша
        FolderQuickService.LOADED_FOLDERS.remove(folder);

        // Обновляем все открытые вкладки редактора чертежей
        for(Tab tab: CH_TAB_PANE.getTabs()) {
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController) {
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }
}
