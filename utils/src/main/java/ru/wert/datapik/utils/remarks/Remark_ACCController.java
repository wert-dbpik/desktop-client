package ru.wert.datapik.utils.remarks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

public class Remark_ACCController extends FormView_ACCController<Remark> {

    @FXML
    private TextArea taRemarksText;

    @FXML
    private VBox vbRemarksEntryContainer;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    void cancel(ActionEvent event) {

    }


    @Override
    public void init(EOperation operation, IFormView<Remark> formView, ItemCommands<Remark> commands) {

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        return null;
    }

    @Override
    public Remark getNewItem() {
        return null;
    }

    @Override
    public Remark getOldItem() {
        return null;
    }

    @Override
    public void fillFieldsOnTheForm(Remark oldItem) {

    }

    @Override
    public void changeOldItemFields(Remark oldItem) {

    }

    @Override
    public void showEmptyForm() {

    }

    @Override
    public boolean enteredDataCorrect() {
        return false;
    }
}
