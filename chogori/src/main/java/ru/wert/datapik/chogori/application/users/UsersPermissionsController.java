package ru.wert.datapik.chogori.application.users;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ru.wert.datapik.utils.entities.users.PermissionsController;
import ru.wert.datapik.utils.entities.users.User_Controller;
import ru.wert.datapik.utils.entities.users._UserPatch;
import ru.wert.datapik.utils.common.components.ChevronButton;
import ru.wert.datapik.utils.statics.AppStatic;

import java.io.IOException;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class UsersPermissionsController {

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUserPane() {
        _UserPatch usersPatch = new _UserPatch();
        usersParent = usersPatch.getUsersPatch();
        usersButtons = usersPatch.getUsersButtons();
        userController = usersPatch.getUserController();

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
}
