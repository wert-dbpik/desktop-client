package ru.wert.datapik.utils.entities.users;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.entity.models.UserGroup;

import java.util.Arrays;
import java.util.List;

public class PermissionsController {

    @FXML
    private StackPane spPermissions;

    @FXML
    private HBox permitionsButtons;

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

    private UserGroup userGroup;
    private boolean[] oldPermitions;

    @FXML
    void initialize(){
        AnchorPane.setTopAnchor(spPermissions, 0.0);
        AnchorPane.setLeftAnchor(spPermissions, 0.0);
        AnchorPane.setBottomAnchor(spPermissions, 0.0);
        AnchorPane.setRightAnchor(spPermissions, 0.0);
    }

    public void init(UserGroup userGroup){
        this.userGroup = userGroup;

//        saveOldPermitions();

        List<CheckBox> boxes = Arrays.asList(
                chbAdministrate,chbEditUsers,
                chbReadDrafts,chbEditDrafts,chbDeleteDrafts,chbCommentDrafts,
                chbReadProductStructure,chbEditProductStructure,chbDeleteProductStructure,
                chbReadMaterials,chbEditMaterials,chbDeleteMaterials
        );

        if(userGroup != null) {
            chbAdministrate.setSelected(this.userGroup.isAdministrate());
            chbEditUsers.setSelected(this.userGroup.isEditUsers());
            chbReadDrafts.setSelected(this.userGroup.isReadDrafts());
            chbEditDrafts.setSelected(this.userGroup.isEditDrafts());
            chbDeleteDrafts.setSelected(this.userGroup.isDeleteDrafts());
            chbCommentDrafts.setSelected(this.userGroup.isCommentDrafts());
            chbReadProductStructure.setSelected(this.userGroup.isReadProductStructures());
            chbEditProductStructure.setSelected(this.userGroup.isEditProductStructures());
            chbDeleteProductStructure.setSelected(this.userGroup.isDeleteProductStructures());
            chbReadMaterials.setSelected(this.userGroup.isReadMaterials());
            chbEditMaterials.setSelected(this.userGroup.isEditMaterials());
            chbDeleteMaterials.setSelected(this.userGroup.isDeleteMaterials());
        } else {
            for (CheckBox ch : boxes)
                ch.setSelected(false);
        }

    }

    private void saveNewPermitions(){
//        user.get
    }

    private void saveOldPermitions() {
        oldPermitions = new boolean[]{
                userGroup.isAdministrate(),
                userGroup.isAdministrate(),
                userGroup.isAdministrate(),
                userGroup.isAdministrate(),
                userGroup.isAdministrate(),
                userGroup.isAdministrate(),
        };
    }


}