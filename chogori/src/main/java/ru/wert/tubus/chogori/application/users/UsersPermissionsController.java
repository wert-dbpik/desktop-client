package ru.wert.tubus.chogori.application.users;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.interfaces.SearchableTab;
import ru.wert.tubus.client.interfaces.UpdatableTabController;
import ru.wert.tubus.chogori.entities.users.PermissionsController;
import ru.wert.tubus.chogori.entities.users.User_Controller;
import ru.wert.tubus.chogori.entities.users.User_TableView;
import ru.wert.tubus.chogori.entities.users._UserPatch;
import ru.wert.tubus.chogori.components.ChevronButton;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.images.BtnImages;

import java.io.IOException;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class UsersPermissionsController implements SearchableTab, UpdatableTabController {

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

    Button btnOK = new Button("СОХРАНИТЬ");

    @FXML
    void initialize(){

        createChevronButtons();

        createUserPane();

        createPermissionsPane();


    }

    private void createPermissionsPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/permissions.fxml"));
            permissionsParent = loader.load();
            apPermissions.getChildren().add(permissionsParent);
            permissionsController = loader.getController();

            permissionsController.setTableView(userTable);
            permissionsController.createSaveButton(btnOK, permissionsParent);

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

        userTable.updateTableView();

        Platform.runLater(()->userTable.requestFocus());
    }

    /**
     * Метод создает кнопки управления тип << >>, для управления сплит панелью
     */
    private void createChevronButtons() {

        divider = splitPane.getDividers().get(0);

        dividerButton = new ChevronButton(
                "Развернуть", BtnImages.BTN_CHEVRON_RIGHT_IMG, 0.8,
                "Свернуть", BtnImages.BTN_CHEVRON_LEFT_IMG, 0.6,
                divider);

    }

    @Override
    public void tuneSearching() {
        Platform.runLater(()->userTable.requestFocus());
        CH_SEARCH_FIELD.changeSearchedTableView(userTable, "ПОЛЬЗОВАТЕЛЬ");
    }


    @Override
    public void updateTab() {
        User user = userTable.getSelectionModel().getSelectedItem();
        userTable.setItems(FXCollections.observableArrayList(ChogoriServices.CH_USERS.findAll()));
        if(user != null)
            userTable.getSelectionModel().select(user);
        userTable.refresh();
    }
}
