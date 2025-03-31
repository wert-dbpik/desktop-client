package ru.wert.tubus.chogori.chat.socketwork.messageHandlers;

import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.chogori.tabs.AppTab;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

/**

 Класс-обработчик сообщений об изменениях, получаемых через чат.

 Обеспечивает синхронизацию данных между клиентами в реальном времени.
 */
@Slf4j
public class OtherChangesHandler {

    /**
     * Просто обновляется вкладка с чертежами, т.к. изменения подгружаются по сети
     */
    public static void handle() {

        for (Tab tab : CH_TAB_PANE.getTabs()) {
            if (((AppTab) tab).getTabController() instanceof DraftsEditorController) {
                ((DraftsEditorController) ((AppTab) tab).getTabController()).updateTab();
            }
        }
    }

}
