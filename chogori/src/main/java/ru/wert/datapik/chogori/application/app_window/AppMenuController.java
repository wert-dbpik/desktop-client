package ru.wert.datapik.chogori.application.app_window;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.excel.ExcelChooser;
import ru.wert.datapik.client.entity.models.ChatGroup;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.chat.SideChat;
import ru.wert.datapik.utils.help.About;
import ru.wert.datapik.utils.search.SearchField;
import ru.wert.datapik.winform.statics.WinformStatic;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_CHAT_GROUPS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;
@Slf4j
public class AppMenuController {

    @FXML
    MenuBar menuBar;

    @FXML @Getter
    public HBox hbSearch;

    @FXML
    Button btnChat;

    private User tempUser;
    private boolean open;

    @FXML
    void initialize(){

        createMenu();

        //Создать поле поиска
        SEARCH_CONTAINER = hbSearch;
        PANE_WITH_SEARCH = createSearchField();


        SideChat sideChat = new SideChat();

        btnChat.setText("чат");
        btnChat.setGraphic(new ImageView(CHAT_WHITE_IMG));
        btnChat.setOnAction(event -> {
            open = !open;
            if(open) {
                SP_CHAT.setPrefWidth(250.0);
                SP_CHAT.setMinWidth(250.0);
                SP_CHAT.setMaxWidth(250.0);
                SP_CHAT.getChildren().add(sideChat.getChatVBox());
            }else {
                for(double width = SP_CHAT.getWidth(); width >= 0; width--){
                    SP_CHAT.setPrefWidth(width);
                    SP_CHAT.setMinWidth(width);
                    SP_CHAT.setMaxWidth(width);
                                    }
                SP_CHAT.getChildren().clear();
            }
        });
    }

    /**
     * СОЗДАТЬ МЕНЮ
     */
    private void createMenu() {

        menuBar.getMenus().add(createMainMenu());
        //Чертежи
        if(CH_CURRENT_USER_GROUP.isReadDrafts())
            menuBar.getMenus().add(createDraftsMenu());
        //Изделия
        if(CH_CURRENT_USER_GROUP.isReadProductStructures())
            menuBar.getMenus().add(createEditorMenu());
        //Материалы
        if(CH_CURRENT_USER_GROUP.isReadMaterials())
            menuBar.getMenus().add(createMaterialsMenu());
        //Изделия
//        if(CH_CURRENT_USER_GROUP.isReadProductStructures())
//            menuBar.getMenus().add(createEditorMenu());
        //Админ
        if(CH_CURRENT_USER_GROUP.isAdministrate())
            menuBar.getMenus().add(createAdminMenu());
        //Помощь
        menuBar.getMenus().add(createHelpMenu());
    }

    /**
     * ПОЛЕ ПОИСКА в одной строке с главным меню
     */
    private HBox createSearchField() {

        ImageView search = new ImageView(BTN_SEARCH_IMG);

        HBox hbox = new HBox();

        CH_SEARCH_FIELD = new SearchField();
        CH_SEARCH_FIELD.setPrefWidth(300);
        Button btnClean = new Button();
        btnClean.setOnAction((e)->{
            CH_SEARCH_FIELD.setText("");
            CH_SEARCH_FIELD.requestFocus();
        });
        btnClean.setGraphic(new ImageView(BTN_CLEAN_IMG_W));
        hbox.getChildren().addAll(search, CH_SEARCH_FIELD, btnClean);
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

        MenuItem settings = new MenuItem("Настройки");
        settings.setOnAction(this::openSettings);

        MenuItem changePassword = new MenuItem("Сменить пароль");
        changePassword.setOnAction(this::changePassword);

        MenuItem changeUserItem = new MenuItem("Сменить пользователя");
        changeUserItem.setOnAction(this::changeUser);

        MenuItem chatItem = new MenuItem("Чат");
        chatItem.setOnAction(this::openChat);

        MenuItem updateData = new MenuItem("Обновить данные");
        updateData.setOnAction(this::updateData);

        MenuItem exitItem = new MenuItem("Выйти");
        exitItem.setOnAction(this::exit);


        mainMenu.getItems().add(changeUserItem);
        mainMenu.getItems().add(changePassword);
        mainMenu.getItems().add(settings);
//        if(!CH_CURRENT_USER.getName().equals("Гость"))
//            mainMenu.getItems().add(chatItem);
        mainMenu.getItems().add(updateData);
        mainMenu.getItems().add(exitItem);

        return mainMenu;
    }

    /**
     * ОТКРЫТЬ ЧАТ
     */
    private void openChat(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/chat/chat.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Чат", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * ОБНОВИТЬ ДАННЫЕ
     */
    private void updateData(ActionEvent actionEvent) {

        Task<Void> updateTask = new TaskUpdateData();
        Thread t = new Thread(updateTask);
        t.setDaemon(true);
        t.start();
    }

    /**
     * СМЕНИТЬ ПАРОЛЬ
     */
    private void changePassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/password/changePassword.fxml"));
            Parent parent = loader.load();

            new WindowDecoration("Смена пароля", parent, false, WF_MAIN_STAGE, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * -- ЗАВЕРШЕНИЕ ПРОГРАММЫ
     */
    private void exit(Event e) {
        WinformStatic.exitApplication(e);

    }

    /**
     * НАСТРОЙКИ
     */
    private void openSettings(Event e){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/settings/settings.fxml"));
            Parent parent = loader.load();

            new WindowDecoration("Настройки", parent, false, WF_MAIN_STAGE, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * -- СМЕНИТЬ ПОЛЬЗОВАТЕЛЯ
     */
    private void changeUser(Event event) {
        //Сохраняем пользователя на случай, если он передумает
        tempUser = CH_CURRENT_USER;


        //Загружаем loginWindow
        try {
            FXMLLoader loginWindowLoader = new FXMLLoader(getClass().getResource("/chogori-fxml/login/login.fxml"));
            Parent loginWindow = loginWindowLoader.load();
            CH_TAB_PANE.getTabs().clear();
            SEARCH_CONTAINER.getChildren().clear();
            menuBar.getMenus().clear();
            CH_DECORATION_ROOT_PANEL.getChildren().add(loginWindow);
            //При нажатии ESCAPE возвращаем прежнего пользователя
            CH_DECORATION_ROOT_PANEL.setOnKeyPressed((event1 -> {
                if(event1.getCode().equals(KeyCode.ESCAPE)){
                    CH_DECORATION_ROOT_PANEL.getChildren().removeAll(loginWindow);
                    CH_CURRENT_USER = tempUser;
                    CH_CURRENT_USER_GROUP = CH_CURRENT_USER.getUserGroup();
                    createMenu();
                }
            }));

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

        MenuItem changeHistoryItem = new MenuItem("История изменений");
        changeHistoryItem.setOnAction(this::openChangeHistory);

        draftsMenu.getItems().add(draftsCabinetItem);
        draftsMenu.getItems().add(draftsItem);
        draftsMenu.getItems().add(new SeparatorMenuItem());
        draftsMenu.getItems().add(changeHistoryItem);

        return draftsMenu;
    }

    /**
     * -- ИСТОРИЯ ИЗМЕНЕНИЙ
     */
    private void openChangeHistory(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/logging/changeHistory.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("История изменений", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

//        MenuItem catalogOfProductItem = new MenuItem("Каталог изделий");
//        catalogOfProductItem.setOnAction(this::openCatalogOfProducts);

        MenuItem openExcelItem = new MenuItem("Открыть файл Excel");
        openExcelItem.setOnAction(this::openExcelFile);

//        editorMenu.getItems().add(catalogOfProductItem);
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
    public void openExcelFile(ActionEvent event) {
        File chosenFile = new ExcelChooser().choose();
        if(chosenFile == null) return;
        Task<Void> openExcelFile = new TaskOpenExcelFile(chosenFile);

        Thread t = new Thread(openExcelFile);
        t.setDaemon(true);
        t.start();
    }

    //########################   АДМИН    ###########################

    /**
     * МЕНЮ АДМИНИСТРАТОРА
     */
    private Menu createAdminMenu() {

        Menu adminMenu = new Menu("Админ");

        MenuItem usersItem = new MenuItem("Пользователи");
        usersItem.setOnAction(this::openUsers);

        MenuItem userGroupsItem = new MenuItem("Группы пользователей");
        userGroupsItem.setOnAction(this::openUserGroups);

        MenuItem logsItem = new MenuItem("Логи");
        logsItem.setOnAction(this::openLogs);

        MenuItem catalogOfFolders = new MenuItem("Каталог папок");
        catalogOfFolders.setOnAction(this::openCatalogOfFolders);

        MenuItem test = new MenuItem("ТЕСТ");
        test.setOnAction(this::makeTest);

        adminMenu.getItems().add(usersItem);
        adminMenu.getItems().add(userGroupsItem);
        adminMenu.getItems().add(logsItem);
        adminMenu.getItems().add(new SeparatorMenuItem());
        adminMenu.getItems().add(catalogOfFolders);
        adminMenu.getItems().add(test);


        return adminMenu;
    }

    /**
     * -- ЛОГИ
     */
    private void openLogs(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/logging/logging.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Логирование", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            CH_TAB_PANE.createNewTab("Пакеты", parent, true, null, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void makeTest(ActionEvent event){
        ChatGroup group = new ChatGroup();
        group.setName("#1#96");
        group.setUser(CH_CURRENT_USER);

        ChatGroup newGroup = CH_CHAT_GROUPS.save(group);
    }

    //########################   ПОМОЩЬ    ###########################

    /**
     * МЕНЮ ПОМОЩЬ
     */
    private Menu createHelpMenu() {

        Menu helpMenu = new Menu("Помощь");

        MenuItem downloadLastVersion = new MenuItem("Скачать последнюю версию");
        downloadLastVersion.setOnAction(this::downloadLastVersion);

        MenuItem helpVideosOnline = new MenuItem("Обучающее видео");
        helpVideosOnline.setOnAction(this::openHelpVideosOnline);

        MenuItem aboutItem = new MenuItem("О программе...");
        aboutItem.setOnAction(this::openAbout);

        MenuItem test = new MenuItem("ТЕСТ");
        test.setOnAction(this::makeTest);

        helpMenu.getItems().addAll(downloadLastVersion, helpVideosOnline, aboutItem);
//        helpMenu.getItems().add(test);

        return helpMenu;
    }

    /**
     * Метод сохраняет файл новой версии программы в выбранную директори
     * В качествое исходной директории предлагается использовать Рабочиц стол
     */
    private void downloadLastVersion(ActionEvent actionEvent){

        new TaskDownloadNewVersion();
    }

    /**
     * -- О ПРОГРАММЕ...
     */
    private void openAbout(ActionEvent event) {
        new About().create();

    }

    /**
     * ОБУЧАЮЩЕЕ ВИДЕО ОНЛАЙН
     */
    private void openHelpVideosOnline(ActionEvent event) {
        try {
            URI uri = new URI("https://www.youtube.com/playlist?list=PLlXRdu_fwDGUorUERVsuC1JTjQXAeNp0E");
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
