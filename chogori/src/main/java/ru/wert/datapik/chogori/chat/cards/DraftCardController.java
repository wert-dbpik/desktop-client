package ru.wert.datapik.chogori.chat.cards;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import ru.wert.datapik.chogori.common.components.VBoxPassport;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.winform.enums.EDraftType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DRAFTS;

public class DraftCardController {

    @FXML
    private VBox vbDraftsName;

    @FXML
    private AnchorPane draftChatCard;

    public void init(String strId){
        Draft draft = CH_DRAFTS.findById(Long.valueOf(strId));
        Passport passport = draft.getPassport();
        VBox box = new VBox();
        VBoxPassport vBoxPassport = new VBoxPassport(passport, "00");
        String type = EDraftType.getDraftTypeById(draft.getDraftType()).getTypeName();
        String page = String.valueOf(draft.getPageNumber());
        Label lblTypeAndPage = new Label(type + ", стр." + page);
        box.getChildren().addAll(vBoxPassport, lblTypeAndPage);
        box.setId("draftInChat");
        vbDraftsName.getChildren().add(box);
        vbDraftsName.setOnMouseClicked(e->{
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                AppStatic.openDraftsInNewTabs(Collections.singletonList(draft));
            }
        });

    }
}
