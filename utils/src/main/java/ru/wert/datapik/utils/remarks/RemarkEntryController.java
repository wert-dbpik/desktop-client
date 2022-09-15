package ru.wert.datapik.utils.remarks;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.Remark;

public class RemarkEntryController {


    @FXML
    private TextArea taRemarksText;

    @FXML
    private Label lblRemarksDate;

    @FXML
    private Label lblRemarksAuthor;

    @FXML
    private VBox vbRemarksEntryContainer;

    void init(Remark remark){
        lblRemarksAuthor.setText(remark.getUser().getName());
        lblRemarksDate.setText(remark.getCreationTime());
        taRemarksText.setText(remark.getText());

    }
}
