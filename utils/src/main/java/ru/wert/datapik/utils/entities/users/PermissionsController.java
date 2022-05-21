package ru.wert.datapik.utils.entities.users;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_USER_GROUPS;

public class PermissionsController<P extends Item> {

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

    @FXML
    private CheckBox chbLogging;

    @Getter private UserGroup userGroup;

    @Setter
    RoutineTableView<User> tableView;
    @Getter private List<CheckBox> boxes;
    private Map<CheckBox, Boolean> oldMap = new HashMap<>();
    private Map<CheckBox, Boolean> newMap = new HashMap<>();

    private Item selectedItem;

    @FXML
    void initialize(){
        AnchorPane.setTopAnchor(spPermissions, 0.0);
        AnchorPane.setLeftAnchor(spPermissions, 0.0);
        AnchorPane.setBottomAnchor(spPermissions, 0.0);
        AnchorPane.setRightAnchor(spPermissions, 0.0);

        boxes = Arrays.asList(
                chbAdministrate,chbEditUsers,
                chbReadDrafts,chbEditDrafts,chbDeleteDrafts,chbCommentDrafts,
                chbReadProductStructure,chbEditProductStructure,chbDeleteProductStructure,
                chbReadMaterials,chbEditMaterials,chbDeleteMaterials,
                chbLogging
        );

    }

    public void init(UserGroup userGroup){
        this.userGroup = userGroup;

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

        if(tableView instanceof User_TableView)
            chbLogging.setSelected(tableView.getSelectionModel().getSelectedItem().isLogging());

        savePermissions(oldMap);

    }

    private void savePermissions(Map<CheckBox, Boolean> map){

        map.put(chbAdministrate, chbAdministrate.isSelected());
        map.put(chbEditUsers, chbEditUsers.isSelected());
        map.put(chbReadDrafts, chbReadDrafts.isSelected());
        map.put(chbEditDrafts, chbEditDrafts.isSelected());
        map.put(chbDeleteDrafts, chbDeleteDrafts.isSelected());
        map.put(chbCommentDrafts, chbCommentDrafts.isSelected());
        map.put(chbReadProductStructure, chbReadProductStructure.isSelected());
        map.put(chbEditProductStructure, chbEditProductStructure.isSelected());
        map.put(chbDeleteProductStructure, chbDeleteProductStructure.isSelected());
        map.put(chbReadMaterials, chbReadMaterials.isSelected());
        map.put(chbEditMaterials, chbEditMaterials.isSelected());
        map.put(chbDeleteMaterials, chbDeleteMaterials.isSelected());
        map.put(chbLogging, chbLogging.isSelected());

    }

    public boolean permissionsWereChanged(){
        savePermissions(newMap);
        return !oldMap.equals(newMap);
    }

    public void saveNewPermissions(){
        userGroup.setAdministrate(chbAdministrate.isSelected());
        userGroup.setEditUsers(chbEditUsers.isSelected());
        userGroup.setReadDrafts(chbReadDrafts.isSelected());
        userGroup.setEditDrafts(chbEditDrafts.isSelected());
        userGroup.setDeleteDrafts(chbDeleteDrafts.isSelected());
        userGroup.setCommentDrafts(chbCommentDrafts.isSelected());
        userGroup.setReadProductStructures(chbReadProductStructure.isSelected());
        userGroup.setEditProductStructures(chbEditProductStructure.isSelected());
        userGroup.setDeleteProductStructures(chbDeleteProductStructure.isSelected());
        userGroup.setReadMaterials(chbReadMaterials.isSelected());
        userGroup.setEditMaterials(chbEditMaterials.isSelected());
        userGroup.setDeleteMaterials(chbDeleteMaterials.isSelected());

        CH_USER_GROUPS.update(userGroup);

        if(tableView instanceof User_TableView){
            User user = tableView.getSelectionModel().getSelectedItem();
            user.setLogging(chbLogging.isSelected());
            CH_USERS.update(user);

            tableView.setItems(FXCollections.observableArrayList(CH_USERS.findAll()));
            tableView.getSelectionModel().select(user);
        }

        savePermissions(oldMap); //Восстанавливаем

    }

    public void createSaveButton(final Button btnOK, Parent permissionsParent) {
        HBox hboxOK = (HBox) permissionsParent.lookup("#hboxOK");
        hboxOK.getChildren().add(btnOK);
        btnOK.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        btnOK.setVisible(false);
        btnOK.setDisable(true);

        List<CheckBox> boxes = getBoxes();
        for (CheckBox box : boxes) {
            box.setOnAction(event -> {
                if (getUserGroup() != null) {
                    if (permissionsWereChanged()) {
                        btnOK.setVisible(true);
                        btnOK.setDisable(false);
                        btnOK.requestFocus();
                    } else {
                        btnOK.setVisible(false);
                        btnOK.setDisable(true);
                    }
                }
            });
        }
        btnOK.setOnAction(e -> {
            saveNewPermissions();
            btnOK.setVisible(false);
            btnOK.setDisable(true);
        });
    }

}