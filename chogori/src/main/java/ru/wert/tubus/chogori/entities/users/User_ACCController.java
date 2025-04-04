package ru.wert.tubus.chogori.entities.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.interfaces.IFormView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;

public class User_ACCController extends FormView_ACCController<User> {

    @FXML
    private TextField txtFldUserName, txtFldPassword;

    @FXML
    private ComboBox<UserGroup> cmbxGroup;

    @FXML
    private StackPane spIndicator;

    @FXML
    private Button btnOk;

    private Item focusedItem;


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

        //Инициализация комбобокса с группами юзеров
        ObservableList<UserGroup> list = FXCollections.observableArrayList(ChogoriServices.CH_USER_GROUPS.findAll());
        cmbxGroup.setItems(list);

    }

    @Override
    public void init(EOperation operation, IFormView<User> formView, ItemCommands<User> commands){
        super.initSuper(operation, formView, commands, ChogoriServices.CH_USERS);
        setInitialValues();

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(txtFldUserName.getText());
        notNullFields.add(txtFldPassword.getText());
        if (cmbxGroup.getValue() == null) notNullFields.add("");
        else
            notNullFields.add(cmbxGroup.getValue().toString());
        return notNullFields;
    }

    @Override
    public User getNewItem() {
        return new User(
            txtFldUserName.getText().replaceAll("[\\s]{2,}", " ").trim(),
            txtFldPassword.getText(),
            cmbxGroup.getValue(),
                true,
                true,
                false
        );
    }

    @Override
    public User getOldItem() {
        return formView.getAllSelectedItems().get(0);
    }

    @Override
    public void fillFieldsOnTheForm(User oldItem) {
        User oldUser = (User) oldItem;
        txtFldUserName.setText(oldUser.getName());
        txtFldPassword.setText(oldUser.getPassword());
        cmbxGroup.setValue(oldUser.getUserGroup());
    }

    @Override
    public void changeOldItemFields(User oldItem) {
        User oldUser = (User) oldItem;
        oldUser.setName(txtFldUserName.getText());
        oldUser.setPassword(txtFldPassword.getText());
        oldUser.setUserGroup(cmbxGroup.getValue());
    }

    @Override
    public void showEmptyForm() {
        txtFldUserName.setText("");
        txtFldPassword.setText("");
        cmbxGroup.setValue(null);
    }

    @Override
    public boolean enteredDataCorrect() {
        return true;
    }

}
