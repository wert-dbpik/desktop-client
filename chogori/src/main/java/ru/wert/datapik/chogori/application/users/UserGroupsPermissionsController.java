package ru.wert.datapik.chogori.application.users;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;
import ru.wert.datapik.utils.common.components.ChevronButton;
import ru.wert.datapik.utils.entities.user_groups.UserGroup_Controller;
import ru.wert.datapik.utils.entities.user_groups.UserGroup_TableView;
import ru.wert.datapik.utils.entities.user_groups._UserGroupPatch;
import ru.wert.datapik.utils.entities.users.PermissionsController;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.BTN_CHEVRON_LEFT_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_CHEVRON_RIGHT_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_USER_GROUPS;

public class UserGroupsPermissionsController implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apUsers;

    @FXML
    private AnchorPane apPermissions;

    @FXML private SplitPane splitPane;

    private SplitPane.Divider divider;
    private Parent userGroupsParent;
    private Button dividerButton;
    private UserGroup_Controller userGroupController;
    private HBox userGroupsButtons;
    private AnchorPane apUserGroupsPatch;

    private Parent permissionsParent;
    private PermissionsController permissionsController;

    private UserGroup_TableView userGroupTable;
    private User selectedUser;

    private Button btnOK = new Button("Сохранить");

    @FXML
    void initialize(){

        createChevronButtons();

        createUserGroupPane();

        createPermissionsPane();

    }

    private void createPermissionsPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/users/permissions.fxml"));
            permissionsParent = loader.load();
            apPermissions.getChildren().add(permissionsParent);
            permissionsController = loader.getController();

            permissionsController.setTableView(userGroupTable);
            permissionsController.createSaveButton(btnOK, permissionsParent);

            ((VBox)permissionsParent.lookup("#vbUserPermissions")).setVisible(false);
            ((VBox)permissionsParent.lookup("#vbUserPermissions")).setDisable(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createUserGroupPane() {
        _UserGroupPatch usersGroupPatch = new _UserGroupPatch();
        userGroupsParent = usersGroupPatch.getUserGroupsPatch();
        userGroupsButtons = usersGroupPatch.getUsersButtons();
        userGroupController = usersGroupPatch.getUserGroupController();
        userGroupTable = userGroupController.getUserGroupTableView();
        userGroupTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != oldValue)
            permissionsController.init(newValue);
        });



        AnchorPane userAnchorPane = userGroupController.getApUserGroupsPatch();
        AppStatic.setNodeInAnchorPane(userAnchorPane);

        userGroupsButtons.getChildren().add(dividerButton);

        apUsers.getChildren().add(userGroupsParent);
    }

    /**
     * Метод создает кнопки управления тип << >>, для управления сплит панелью
     */
    private void createChevronButtons() {

        divider = splitPane.getDividers().get(0);

        dividerButton = new ChevronButton(
                "Развернуть", BTN_CHEVRON_RIGHT_IMG, 0.8,
                "Свернуть", BTN_CHEVRON_LEFT_IMG, 0.6,
                divider);

    }

    @Override
    public void tuneSearching() {
//        CH_SEARCH_FIELD.setSearchableTableController(userGroupTable);
//        String searchedText = userGroupTable.getSearchedText();
//        if (searchedText.equals(""))
//            CH_SEARCH_FIELD.setPromptText("ГРУППА ПОЛЬЗОВАТЕЛЕЙ");
//        else
//            CH_SEARCH_FIELD.setSearchedText(userGroupTable.getSearchedText());
    }

    @Override
    public void updateTab() {
        UserGroup group = userGroupTable.getSelectionModel().getSelectedItem();
        userGroupTable.setItems(CH_USER_GROUPS.findAll());
        if(group != null)
            userGroupTable.getSelectionModel().select(group);
        userGroupTable.refresh();
    }
}
