package ru.wert.datapik.chogori.application.app_window;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.IOException;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_DECORATION_ROOT_PANEL;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;

public class AppMenuController {

    @FXML
    MenuBar menuBar;

    @FXML
    void initialize(){
        menuBar.getMenus().add(createMainMenu());
        //Чертежи
        if(CH_CURRENT_USER.getUserGroup().isReadDrafts())
            menuBar.getMenus().add(createDraftsMenu());
        //Материалы
        if(CH_CURRENT_USER.getUserGroup().isReadMaterials())
            menuBar.getMenus().add(createMaterialsMenu());
        //Админ
        if(CH_CURRENT_USER.getUserGroup().isAdministrate())
            menuBar.getMenus().add(createAdminMenu());
    }

    /**
     * МЕНЮ МАТЕРИАЛОВ
     */
    private Menu createMaterialsMenu() {

        Menu materialsMenu = new Menu("Материалы");

        MenuItem catalogOfMaterialsItem = new MenuItem("Каталог материалов");
        catalogOfMaterialsItem.setOnAction(this::openCatalogOfMaterials);

        materialsMenu.getItems().add(catalogOfMaterialsItem);

        return materialsMenu;
    }

    /**
     * Каталог материалов
     */
    private void openCatalogOfMaterials(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfMaterials/catalogOfMaterials.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Материалы", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * МЕНЮ ЧЕРТЕЖЕЙ
     */
    private Menu createDraftsMenu() {

        Menu draftsMenu = new Menu("Чертежи");

        MenuItem draftsCabinetItem = new MenuItem("Картотека");
        draftsCabinetItem.setOnAction(this::openFileCabinet);

        MenuItem draftsItem = new MenuItem("Чертежи");
        draftsItem.setOnAction(this::openDrafts);

        draftsMenu.getItems().add(draftsCabinetItem);
        if(CH_CURRENT_USER.getUserGroup().isEditDrafts())
            draftsMenu.getItems().add(draftsItem);

        return draftsMenu;
    }

    /**
     * МЕНЮ АДМИНИСТРАТОРА
     */
    private Menu createAdminMenu() {

        Menu adminMenu = new Menu("Админ");

        MenuItem usersItem = new MenuItem("Пользователи");
        usersItem.setOnAction(this::openUsers);

        MenuItem userGroupsItem = new MenuItem("Группы Пользователей");
        userGroupsItem.setOnAction(this::openUserGroups);

        adminMenu.getItems().add(usersItem);
        adminMenu.getItems().add(userGroupsItem);

        return adminMenu;
    }

    /**
     * МЕНЮ ОСНОВНОЕ
     */
    private Menu createMainMenu() {

        Menu mainMenu = new Menu("Общее");

        MenuItem changeUserItem = new MenuItem("Сменить пользователя");
        changeUserItem.setOnAction(this::changeUser);

        MenuItem exitItem = new MenuItem("Выйти");
        exitItem.setOnAction(this::exit);

        mainMenu.getItems().add(changeUserItem);
        mainMenu.getItems().add(exitItem);

        return mainMenu;
    }

    private void exit(Event e){
        System.exit(0);
    }

    private void changeUser(Event event) {
        //Загружаем loginWindow
        try {
            FXMLLoader loginWindowLoader = new FXMLLoader(getClass().getResource("/chogori-fxml/login/login.fxml"));
            Parent loginWindow = loginWindowLoader.load();
            CH_TAB_PANE.getTabs().clear();
            menuBar.getMenus().clear();
            CH_DECORATION_ROOT_PANEL.getChildren().add(loginWindow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/usersPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Пользователи", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openUserGroups(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/userGroupsPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Группы Пользователей", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void openFileCabinet(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/passports/passportsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Картотека", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDrafts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/draftsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/drafts-dark.css").toString());
            CH_TAB_PANE.createNewTab("Чертежи", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
