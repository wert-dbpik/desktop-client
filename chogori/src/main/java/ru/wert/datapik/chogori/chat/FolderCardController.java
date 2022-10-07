package ru.wert.datapik.chogori.chat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.application.app_window.AppMenuController;
import ru.wert.datapik.chogori.common.components.VBoxPassport;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.winform.enums.EDraftType;

import java.util.Collections;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DRAFTS;

public class FolderCardController {

    @FXML
    private VBox vbDraftsName;

    @FXML
    private AnchorPane draftChatCard;

    void init(String strId){
        Draft draft = CH_DRAFTS.findById(Long.valueOf(strId));
        Passport passport = draft.getPassport();
        VBox box = new VBox();
        VBoxPassport vBoxPassport = new VBoxPassport(passport, "00");
        box.getChildren().addAll(vBoxPassport);
        box.setId("draftInChat");
        vbDraftsName.getChildren().add(box);
        vbDraftsName.setOnMouseClicked(e->{
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                AppMenuController.openDrafts(e);

            }
        });

    }
}
