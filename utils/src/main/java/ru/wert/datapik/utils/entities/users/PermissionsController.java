package ru.wert.datapik.utils.entities.users;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.models.UserGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsController {

    @FXML
    private StackPane stackPane;

    @FXML
    private HBox permitionsButtons;

    @FXML
    private CheckBox chbAdministrate;

    @FXML
    private CheckBox chbEditUsers;

    @FXML
    private CheckBox chbEditDrafts;

    @FXML
    private CheckBox chbCommentDrafts;

    @FXML
    private CheckBox chbEditProducts;

    @FXML
    private CheckBox chbEditMaterials;

    private User user;
    private UserGroup userGroup;
    private boolean[] oldPermitions;

    @FXML
    void initialize(){
        AnchorPane.setTopAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
    }

    public void init(User user){
        this.user = user;
        this.userGroup = user.getUserGroup();

        saveOldPermitions();

        List<CheckBox> boxes = Arrays.asList(chbAdministrate, chbEditUsers, chbEditDrafts,
                chbCommentDrafts, chbEditProducts, chbEditMaterials);

        if(user != null) {
            chbAdministrate.setSelected(user.getUserGroup().isAdministrate());
            chbEditUsers.setSelected(user.getUserGroup().isEditUsers());
            chbEditDrafts.setSelected(user.getUserGroup().isEditDrafts());
            chbCommentDrafts.setSelected(user.getUserGroup().isCommentDrafts());
            chbEditProducts.setSelected(user.getUserGroup().isEditProducts());
            chbEditMaterials.setSelected(user.getUserGroup().isEditMaterials());
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
                user.getUserGroup().isAdministrate(),
                user.getUserGroup().isAdministrate(),
                user.getUserGroup().isAdministrate(),
                user.getUserGroup().isAdministrate(),
                user.getUserGroup().isAdministrate(),
                user.getUserGroup().isAdministrate(),
        };
    }


}