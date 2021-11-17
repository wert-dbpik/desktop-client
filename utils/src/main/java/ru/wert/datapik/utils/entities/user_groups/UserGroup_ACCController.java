package ru.wert.datapik.utils.entities.user_groups;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_USER_GROUPS;
import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;

public class UserGroup_ACCController extends FormView_ACCController<UserGroup> {

    @FXML
    private TextField tfUserGroupName;
            
    @FXML
    private CheckBox chbAdministrate;

    @FXML
    private CheckBox chbEditUsers;

    @FXML
    private CheckBox chbReadDrafts;
    
    @FXML
    private CheckBox chbEditDrafts;
    
    @FXML
    private CheckBox chbDeleteDrafts;
        
    @FXML
    private CheckBox chbCommentDrafts;

    @FXML
    private CheckBox chbReadProductStructure;

    @FXML
    private CheckBox chbEditProductStructure;

    @FXML
    private CheckBox chbDeleteProductStructure;

    @FXML
    private CheckBox chbReadMaterials;

    @FXML
    private CheckBox chbEditMaterials;

    @FXML
    private CheckBox chbDeleteMaterials;
    
    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;
    
;



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

        List<CheckBox> boxes = Arrays.asList(
                chbAdministrate,chbEditUsers,
                chbReadDrafts,chbEditDrafts,chbDeleteDrafts,chbCommentDrafts,
                chbReadProductStructure,chbEditProductStructure,chbDeleteProductStructure,
                chbReadMaterials,chbEditMaterials,chbDeleteMaterials
        );

    }

    @Override
    public void init(EOperation operation, IFormView<UserGroup> formView, ItemCommands<UserGroup> commands){
        super.initSuper(operation, formView, commands, CH_USER_GROUPS);
        setInitialValues();

    }

    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(tfUserGroupName.getText());
        return notNullFields;
    }

    @Override
    public UserGroup getNewItem() {
        return new UserGroup(
            tfUserGroupName.getText().replaceAll("[\\s]{2,}", " ").trim(),
                chbAdministrate.isSelected(),chbEditUsers.isSelected(),
                chbReadDrafts.isSelected(),chbEditDrafts.isSelected(),chbDeleteDrafts.isSelected(),chbCommentDrafts.isSelected(),
                chbReadProductStructure.isSelected(),chbEditProductStructure.isSelected(),chbDeleteProductStructure.isSelected(),
                chbReadMaterials.isSelected(),chbEditMaterials.isSelected(),chbDeleteMaterials.isSelected()
        );
    }

    @Override
    public void fillFieldsOnTheForm(UserGroup oldItem) {

        tfUserGroupName.setText(oldItem.getName());
        chbAdministrate.setSelected(oldItem.isAdministrate());
        chbEditUsers.setSelected(oldItem.isEditUsers());
        chbReadDrafts.setSelected(oldItem.isReadDrafts());
        chbEditDrafts.setSelected(oldItem.isEditDrafts());
        chbDeleteDrafts.setSelected(oldItem.isDeleteDrafts());
        chbCommentDrafts.setSelected(oldItem.isCommentDrafts());
        chbReadProductStructure.setSelected(oldItem.isReadProductStructure());
        chbEditProductStructure.setSelected(oldItem.isEditProductStructure());
        chbDeleteProductStructure.setSelected(oldItem.isDeleteProductStructure());
        chbReadMaterials.setSelected(oldItem.isReadMaterials());
        chbEditMaterials.setSelected(oldItem.isEditMaterials());
        chbDeleteMaterials.setSelected(oldItem.isDeleteMaterials());
    }

    @Override
    public void changeOldItemFields(UserGroup oldItem) {

        oldItem.setName(txtFldUserGroupName.getText());
        oldItem.setPassword(txtFldPassword.getText());
        oldItem.setUserGroup(cmbxGroup.getValue());
    }

    @Override
    public void showEmptyForm() {
        tfUserGroupName.setText("");
        chbAdministrate.setSelected(false);
        chbEditUsers.setSelected(false);
        chbReadDrafts.setSelected(false);
        chbEditDrafts.setSelected(false);
        chbDeleteDrafts.setSelected(false);
        chbCommentDrafts.setSelected(false);
        chbReadProductStructure.setSelected(false);
        chbEditProductStructure.setSelected(false);
        chbDeleteProductStructure.setSelected(false);
        chbReadMaterials.setSelected(false);
        chbEditMaterials.setSelected(false);
        chbDeleteMaterials.setSelected(false);
    }

}
