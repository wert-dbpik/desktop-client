package ru.wert.datapik.chogori.application.app_window;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Getter;
import ru.wert.datapik.chogori.application.editor.ExcelChooser;
import ru.wert.datapik.chogori.application.editor.ExcelEditorNewController;
import ru.wert.datapik.utils.help.About;
import ru.wert.datapik.utils.search.SearchField;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.images.BtnImages.BTN_CLEAN_IMG_W;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class AppMenuController {

    @FXML
    MenuBar menuBar;

    @FXML @Getter
    public HBox hbSearch;

    @FXML
    void initialize(){

        menuBar.getMenus().add(createMainMenu());
        //Чертежи
        if(CH_CURRENT_USER.getUserGroup().isReadDrafts())
            menuBar.getMenus().add(createDraftsMenu());
        //Материалы
        if(CH_CURRENT_USER.getUserGroup().isReadMaterials())
            menuBar.getMenus().add(createMaterialsMenu());
        //Изделия
        if(CH_CURRENT_USER.getUserGroup().isReadProductStructures())
            menuBar.getMenus().add(createEditorMenu());
        //Админ
        if(CH_CURRENT_USER.getUserGroup().isAdministrate())
            menuBar.getMenus().add(createAdminMenu());
        //Помощь
            menuBar.getMenus().add(createHelpMenu());

        //Создать поле поиска
        SEARCH_CONTAINER = hbSearch;
        PANE_WITH_SEARCH = createSearchField();


    }

    /**
     * ПОЛЕ ПОИСКА в одной строке с главным меню
     */
    private HBox createSearchField() {

        HBox hbox = new HBox();

        CH_SEARCH_FIELD = new SearchField();
        CH_SEARCH_FIELD.setPrefWidth(300);
        Button btnClean = new Button();
        btnClean.setOnAction((e)->{
            CH_SEARCH_FIELD.setText("");
            CH_SEARCH_FIELD.requestFocus();
        });
        btnClean.setGraphic(new ImageView(BTN_CLEAN_IMG_W));
        hbox.getChildren().addAll(CH_SEARCH_FIELD, btnClean);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setSpacing(2);
        hbox.getStylesheets().add(getClass().getResource("/chogori-css/toolpane-dark.css").toString());

        return hbox;

    }

    //######################   ОБЩЕЕ  #############################

    /**
     * МЕНЮ ОБЩЕЕ
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


    /**
     * -- ЗАВЕРШЕНИЕ ПРОГРАММЫ
     */
    private void exit(Event e){
        System.exit(0);
    }

    /**
     * -- СМЕНИТЬ ПОЛЬЗОВАТЕЛЯ
     */
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

   //########################   ЧЕРТЕЖИ   ############################

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
     * -- КАРТОТЕКА
     */
    private void openFileCabinet(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/passports/passportsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Картотека", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- ЧЕРТЕЖИ
     */
    private void openDrafts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/draftsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/drafts-dark.css").toString());
            CH_TAB_PANE.createNewTab("Чертежи", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //######################   МАТЕРИАЛЫ   ##########################

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
     * -- МАТЕРИАЛЫ
     */
    private void openCatalogOfMaterials(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfMaterials/catalogOfMaterials.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Материалы", parent, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //######################   РЕДАКТОР   ##########################

    /**
     * МЕНЮ ИЗДЕЛИЙ
     */
    private Menu createEditorMenu() {

        Menu editorMenu = new Menu("Изделия");

        MenuItem catalogOfProductItem = new MenuItem("Каталог изделий");
        catalogOfProductItem.setOnAction(this::openCatalogOfProducts);

        MenuItem openExcelItem = new MenuItem("Открыть файл Excel");
        openExcelItem.setOnAction(this::openCatalogOfProducts);

        editorMenu.getItems().add(catalogOfProductItem);
        if (CH_CURRENT_USER.getUserGroup().isEditProductStructures())
            editorMenu.getItems().add(openExcelItem);

        return editorMenu;
    }

    /**
     * -- КАТАЛОГ ИЗДЕЛИЙ
     */
    void openCatalogOfProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfProducts/catalogOfProducts.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Изделия", parent, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- ОТКРЫТЬ ФАЙЛ EXCEL
     */
    public void openExcelFile() {
//        EditorPatch.getInstance().invokeFileChooser();
        File chosenFile = new ExcelChooser().choose();
        if(chosenFile == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/excel/excelEditorNew.fxml"));
            Parent parent = loader.load();
            ExcelEditorNewController controller = loader.getController();
            controller.init(chosenFile);
//            String fileName = controller.getFileName();

            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/drafts-dark.css").toString());
            CH_TAB_PANE.createNewTab(chosenFile.getName(), parent, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //########################   АДМИН    ###########################

    /**
     * МЕНЮ АДМИНИСТРАТОРА
     */
    private Menu createAdminMenu() {

        Menu adminMenu = new Menu("Админ");

        MenuItem usersItem = new MenuItem("Пользователи");
        usersItem.setOnAction(this::openUsers);

        MenuItem userGroupsItem = new MenuItem("Группы Пользователей");
        userGroupsItem.setOnAction(this::openUserGroups);

        MenuItem catalogOfFolders = new MenuItem("Каталог папок");
        catalogOfFolders.setOnAction(this::openCatalogOfFolders);



        adminMenu.getItems().add(usersItem);
        adminMenu.getItems().add(userGroupsItem);
        adminMenu.getItems().add(new SeparatorMenuItem());
        adminMenu.getItems().add(catalogOfFolders);

        return adminMenu;
    }

    /**
     * -- ПОЛЬЗОВАТЕЛИ
     */
    private void openUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/usersPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Пользователи", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- ГРУППЫ ПОЛЬЗОВАТЕЛЕЙ
     */
    private void openUserGroups(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/userGroupsPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Группы Пользователей", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * -- КАТАЛОГ ПАПОК
     */
    void openCatalogOfFolders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfFolders/catalogOfFolders.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Пакеты", parent, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //########################   ПОМОЩЬ    ###########################

    /**
     * МЕНЮ ПОМОЩЬ
     */
    private Menu createHelpMenu() {

        Menu helpMenu = new Menu("Помощь");

        MenuItem aboutItem = new MenuItem("О программе...");
        aboutItem.setOnAction(this::openAbout);

        helpMenu.getItems().add(aboutItem);

        return helpMenu;
    }

    /**
     * -- О ПРОГРАММЕ...
     */
    private void openAbout(ActionEvent event) {
        new About().create();

    }

}