package ru.wert.datapik.utils.entities.users;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class User_Controller {

    @FXML
    private AnchorPane apUsersPatch;

    @FXML
    private VBox vbUsers;

    @FXML
    private HBox usersButtons;

    private User_TableView userTableView;


    @FXML
    void initialize() {
        //Создаем панели инструментов
        createUsers_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createUsers_TableView();
    }


    public HBox getUsersButtons() {
        return usersButtons;
    }


    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createUsers_TableView() {
        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditUsers();
        userTableView = new User_TableView("ПОЛЬЗОВАТЕЛЬ", useContextMenu);
        userTableView.updateView();
        VBox.setVgrow(userTableView, Priority.ALWAYS);
        vbUsers.getChildren().add(userTableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createUsers_ToolBar(){

        // КНОПОК УПРАВЛЕНИЯ НЕТ

//        usersButtons.getChildren().add();
    }

    public AnchorPane getApUsersPatch() {
        return apUsersPatch;
    }

    public User_TableView getUserTableView() {
        return userTableView;
    }
}
