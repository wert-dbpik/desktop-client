package ru.wert.datapik.chogori.chat.cards;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.wert.datapik.chogori.application.app_window.AppMenuController;
import ru.wert.datapik.chogori.application.drafts.DraftsEditorController;
import ru.wert.datapik.chogori.application.drafts.OpenDraftsEditorTask;
import ru.wert.datapik.chogori.common.components.VBoxPassport;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.chogori.tabs.AppTab;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.winform.enums.EDraftType;

import java.util.Collections;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DRAFTS;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.chogori.chat.SideChatTalkController.MESSAGE_WIDTH;
import static ru.wert.datapik.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

/**
 * Controller писывает поведение карточки с наименованием комплекта чертежей, передаваемой через ЧАТ
 */
public class FolderCardController {

    @FXML
    private VBox vbFoldersName;

    @FXML
    private AnchorPane folderChatCard;


    public void init(String strId) {
        Folder folder = CH_FOLDERS.findById(Long.valueOf(strId));
        String folderName = folder.getName();
        Label label = new Label(folderName);
        label.setStyle("-fx-padding: 0 10 0 10;");
        label.setWrapText(true);
        label.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        label.setId("draftInChat");

        vbFoldersName.getChildren().add(label);


         /* При двойном клике на карточку, производится проверка
         * если вкладка с чертежами уже открыта, то данные карточки передаются в существующий контроллер
         * если вкладка закрыта, то контроллер вкладки создается вновь*/
        vbFoldersName.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {

                AppTab pane = CH_TAB_PANE.tabIsAvailable("Чертежи");
                if(pane != null){
                    DraftsEditorController controller = (DraftsEditorController) pane.getTabController();
                    controller.openFolderFromChat(folder);
                } else {
                    OpenDraftsEditorTask openDraftsTask = new OpenDraftsEditorTask();
                    openDraftsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                            t -> {
                                DraftsEditorController controller = openDraftsTask.getValue();
                                controller.openFolderFromChat(folder);
                            });

                    Thread thread = new Thread(openDraftsTask);
                    thread.setDaemon(true);
                    thread.start();
                }

            }

        });

    }
}