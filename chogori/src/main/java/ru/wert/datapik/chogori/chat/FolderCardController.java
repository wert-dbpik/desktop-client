package ru.wert.datapik.chogori.chat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.wert.datapik.chogori.application.app_window.AppMenuController;
import ru.wert.datapik.chogori.application.drafts.DraftsEditorController;
import ru.wert.datapik.chogori.common.components.VBoxPassport;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.winform.enums.EDraftType;

import java.util.Collections;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DRAFTS;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.chogori.chat.SideChatTalkController.MESSAGE_WIDTH;
import static ru.wert.datapik.chogori.statics.AppStatic.CHAT_WIDTH;

public class FolderCardController {

    @FXML
    private VBox vbFoldersName;

    @FXML
    private AnchorPane folderChatCard;

    void init(String strId){
        Folder folder = CH_FOLDERS.findById(Long.valueOf(strId));
        String folderName = folder.getName();
        Label label = new Label(folderName);
        label.setWrapText(true);
        label.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        label.setId("draftInChat");

        vbFoldersName.getChildren().add(label);
        vbFoldersName.setOnMouseClicked(e->{
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                DraftsEditorController controller = AppMenuController.openDrafts(e);
                System.out.println(controller);

            }
        });

    }
}
