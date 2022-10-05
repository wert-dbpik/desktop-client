package ru.wert.datapik.chogori.entities.user_groups;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class UserGroup_Controller {

    @FXML
    private AnchorPane apUserGroupsPatch;

    @FXML
    private VBox vbUserGroups;

    @FXML
    private HBox userGroupsButtons;

    private UserGroup_TableView userGroupTableView;


    @FXML
    void initialize() {
        //Создаем панели инструментов
        createUserGroups_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createUserGroups_TableView();
    }


    public HBox getUserGroupsButtons() {
        return userGroupsButtons;
    }


    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createUserGroups_TableView() {

        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditUsers();
        userGroupTableView = new UserGroup_TableView("ГРУППЫ ПОЛЬЗОВАТЕЛЕЙ", useContextMenu);
//        userGroupTableView.updateView();
        VBox.setVgrow(userGroupTableView, Priority.ALWAYS);
        vbUserGroups.getChildren().add(userGroupTableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createUserGroups_ToolBar(){

        // КНОПОК УПРАВЛЕНИЯ НЕТ

//        usersButtons.getChildren().add();
    }

    public AnchorPane getApUserGroupsPatch() {
        return apUserGroupsPatch;
    }

    public UserGroup_TableView getUserGroupTableView() {
        return userGroupTableView;
    }
}
