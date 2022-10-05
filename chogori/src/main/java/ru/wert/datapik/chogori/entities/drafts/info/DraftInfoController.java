package ru.wert.datapik.chogori.entities.drafts.info;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;

import static ru.wert.datapik.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class DraftInfoController {

    @FXML
    private AnchorPane apInfo;

    @FXML
    private Label lblDecNumber;

    @FXML
    private Label lblName;

    @FXML
    private Label lblCreationTime;

    @FXML
    private Label lblTypeStr;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblSource;

    @FXML
    private TextArea taNote;

    @FXML
    void initialize(){
        taNote.setStyle("-fx--: black; -fx-text-fill: white");
    }

    public void init(Draft draft){
        lblDecNumber.setText(draft.getDecimalNumber());

        lblName.setText(draft.getName());

        lblCreationTime.setText(parseLDTtoNormalDate(draft.getStatusTime()) + ", " +
                draft.getCreationUser().getName());

        EDraftType type = EDraftType.getDraftTypeById(draft.getDraftType());
        lblTypeStr.setText(type.getShortName() + "-" + draft.getPageNumber());

        lblStatus.setText(EDraftStatus.getStatusById(draft.getStatus()).getStatusName());

        lblSource.setText(draft.getFolder().toUsefulString());

        taNote.setText(draft.getNote());

        Platform.runLater(()->apInfo.requestFocus());

    }

}
