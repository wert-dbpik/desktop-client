package ru.wert.datapik.chogori.entities.passports;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;

public class Passport_ACCController extends FormView_ACCController<Passport> {

    @FXML
    private TextField txtFldPassportName, txtFldPassword;


    private Item focusedItem;


    @FXML
    void cancel(ActionEvent event) {
        super.cancelPressed(event);

    }

    @FXML
    void ok(ActionEvent event) {
        super.okPressed(event, null, null);
    }

    @FXML
    void initialize() {

    }

    @Override
    public void init(EOperation operation, IFormView<Passport> formView, ItemCommands<Passport> commands){
        super.initSuper(operation, formView, commands, CH_QUICK_PASSPORTS);

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        return notNullFields;
    }

    @Override
    public Passport getNewItem() {
        return new Passport(

        );
    }

    @Override
    public Passport getOldItem() {
        return formView.getAllSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(Passport oldItem) {
        Passport oldPassport = (Passport) oldItem;

    }

    @Override
    public void changeOldItemFields(Passport oldItem) {
        Passport oldPassport = (Passport) oldItem;

    }

    @Override
    public void showEmptyForm() {

    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }


}
