package ru.wert.datapik.chogori.application.users;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.utils.entities.users.PermissionsController;
import ru.wert.datapik.utils.entities.users.User_Controller;
import ru.wert.datapik.utils.entities.users.User_TableView;
import ru.wert.datapik.utils.entities.users._UserPatch;
import ru.wert.datapik.utils.common.components.ChevronButton;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.tabs.SearchablePane;

import java.io.IOException;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class UsersPermissionsController implements SearchablePane{

    @FXML
    private AnchorPane apUsers;

    @FXML
    private AnchorPane apPermissions;

    @FXML private SplitPane splitPane;

    private SplitPane.Divider divider;
    private Parent usersParent;
    private Button dividerButton;
    private User_Controller userController;
    private HBox usersButtons;
    private AnchorPane apUsersPatch;

    private Parent permissionsParent;
    private PermissionsController permissionsController;

    private User_TableView userTable;

    @FXML
    void initialize(){

        createChevronButtons();

        createUserPane();

        createPermissionsPane();


    }

    private void createPermissionsPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/users/permissions.fxml"));
            permissionsParent = loader.load();
            apPermissions.getChildren().add(permissionsParent);
            permissionsController = loader.getController();

//            userTableView.getSelectionModel().select(1);
//            selectedUser = userTableView.getSelectionModel().getSelectedItem();
//            permissionsController.init(selectedUser);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUserPane() {
        _UserPatch usersPatch = new _UserPatch();
        usersParent = usersPatch.getUsersPatch();
        usersButtons = usersPatch.getUsersButtons();
        userController = usersPatch.getUserController();
        userTable = userController.getUserTableView();
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue != oldValue)
            permissionsController.init(newValue.getUserGroup());
        });



        AnchorPane userAnchorPane = userController.getApUsersPatch();
        AppStatic.setNodeInAnchorPane(userAnchorPane);

        usersButtons.getChildren().add(dividerButton);

        apUsers.getChildren().add(usersParent);
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
        CH_SEARCH_FIELD.setSearchableTableController(userTable);
        String searchedText = userTable.getSearchedText();
        if (searchedText.equals(""))
            CH_SEARCH_FIELD.setPromptText("ПОЛЬЗОВАТЕЛЬ");
        else
            CH_SEARCH_FIELD.setSearchedText(userTable.getSearchedText());
    }


}
