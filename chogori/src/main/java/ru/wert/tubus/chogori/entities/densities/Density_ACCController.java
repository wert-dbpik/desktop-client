package ru.wert.tubus.chogori.entities.densities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.components.TFDouble;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.common.utils.DoubleParser;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Density;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_DENSITIES;

public class Density_ACCController extends FormView_ACCController<Density> {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfAmount;

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
        new TFDouble(tfAmount);
    }

    @Override
    public void init(EOperation operation, IFormView<Density> formView, ItemCommands<Density> commands){
        super.initSuper(operation, formView, commands, CH_DENSITIES);
        setInitialValues();

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(tfName.getText());
        notNullFields.add(tfAmount.getText());

        return notNullFields;
    }

    @Override
    public Density getNewItem() {
        return new Density(
                tfName.getText().trim(),
                DoubleParser.getValue(tfAmount),
                taNote.getText()
        );
    }

    @Override
    public Density getOldItem() {
        return formView.getAllSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(Density oldItem) {
        Density oldDensity = (Density) oldItem;
        tfName.setText(oldDensity.getName());
        tfAmount.setText(String.valueOf(oldDensity.getAmount()));
        taNote.setText(oldDensity.getNote());
    }

    @Override
    public void changeOldItemFields(Density oldItem) {
        Density oldDensity = (Density) oldItem;
        oldDensity.setName(tfName.getText().trim());
        oldDensity.setAmount(DoubleParser.getValue(tfAmount));
        oldDensity.setNote(taNote.getText());
    }

    @Override
    public void showEmptyForm() {
        tfName.setText("");
        tfAmount.setText("");
        taNote.setText("");
    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

}
