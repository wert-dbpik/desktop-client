package ru.wert.tubus.chogori.entities.drafts.info;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;

import static ru.wert.tubus.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class DraftInfoController {


    @FXML
    private AnchorPane apInfo;

    @FXML
    private Label lblId;

    @FXML
    private Label lblDecNumber;

    @FXML
    private Label lblName;

    @FXML
    private Label lblSourceFileName;

    @FXML
    private Label lblCreationTime;

    @FXML
    private Label lblTypeStr;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblStatusTime;

    @FXML
    private Label lblSource;

    @FXML
    private TextArea taNote;

    @FXML
    void initialize(){
        taNote.setStyle("-fx--: black; -fx-text-fill: white");
    }

    public void init(Draft draft){

        lblId.setText(String.valueOf(draft.getId()));

        lblDecNumber.setText(draft.getDecimalNumber());

        lblName.setText(draft.getName());

        lblSourceFileName.setText(draft.getInitialDraftName());

        lblCreationTime.setText(parseLDTtoNormalDate(draft.getCreationTime()) + ", " +
                draft.getCreationUser().getName());

        EDraftType type = EDraftType.getDraftTypeById(draft.getDraftType());
        lblTypeStr.setText(type.getShortName() + "-" + draft.getPageNumber());

        lblStatus.setText(EDraftStatus.getStatusById(draft.getStatus()).getStatusName());

        lblStatusTime.setText(parseLDTtoNormalDate(draft.getStatusTime()));

        lblSource.setText(draft.getFolder().toUsefulString());

        taNote.setText(draft.getNote());

        Platform.runLater(()->apInfo.requestFocus());

    }

}
