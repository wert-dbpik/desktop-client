package ru.wert.datapik.utils.entities.passports;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;

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
        super.okPressed(event);
        closeWindow(event);

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


}
