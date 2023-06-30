package ru.wert.tubus.chogori.entities.passports;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;

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
        super.initSuper(operation, formView, commands, ChogoriServices.CH_QUICK_PASSPORTS);

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
