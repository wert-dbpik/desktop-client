package ru.wert.datapik.chogori.entities.prefixes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.IFormView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PREFIXES;

public class Prefix_ACCController extends FormView_ACCController<Prefix> {

    @FXML
    private TextField tfName;

    @FXML
    private TextArea taNote;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;


    @FXML
    void cancel(ActionEvent event) {
        super.cancelPressed(event);

    }

    @FXML
    void ok(ActionEvent event) {
        super.okPressed(event, spIndicator, btnOk);
    }

    @FXML
    void initialize() {

        AppStatic.createSpIndicator(spIndicator);

    }

    @Override
    public void init(EOperation operation, IFormView<Prefix> formView, ItemCommands<Prefix> commands){
        super.initSuper(operation, formView, commands, CH_PREFIXES);
        setInitialValues();

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(tfName.getText());

        return notNullFields;
    }

    @Override
    public Prefix getNewItem() {
        return new Prefix(
            tfName.getText().trim(),
            taNote.getText()
        );
    }

    @Override
    public Prefix getOldItem() {
        return formView.getAllSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(Prefix oldItem) {
        Prefix oldPrefix = (Prefix) oldItem;
        tfName.setText(oldPrefix.getName());
        taNote.setText(oldPrefix.getNote());
    }

    @Override
    public void changeOldItemFields(Prefix oldItem) {
        Prefix oldPrefix = (Prefix) oldItem;
        oldPrefix.setName(tfName.getText().trim());
        oldPrefix.setNote(taNote.getText());
    }

    @Override
    public void showEmptyForm() {
        tfName.setText("");
        taNote.setText("");
    }

    @Override
    public boolean enteredDataCorrect() {
        return tfName.getText().length() <= 5;
    }

}
