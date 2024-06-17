package ru.wert.tubus.chogori.application.app_window;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.StartChogori;
import ru.wert.tubus.chogori.application.drafts.OpenDraftsEditorTask;
import ru.wert.tubus.chogori.application.excel.ExcelChooser;
import ru.wert.tubus.chogori.application.passports.OpenPassportsEditorTask;
import ru.wert.tubus.chogori.components.BtnDoublePro;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.help.About;
import ru.wert.tubus.chogori.search.SearchField;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.winform.statics.WinformStatic;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.images.BtnImages.*;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.*;
import static ru.wert.tubus.chogori.statics.AppStatic.KOMPLEKT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.*;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.tubus.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
@Slf4j
public class AppMenuController {

    @FXML
    MenuBar menuBar;

    @FXML @Getter
    public HBox hbSearch;

    @FXML
    Button btnChat;

    @FXML
    Button btnCloseTab;

    private User tempUser;
    private boolean open;

    @FXML
    void initialize(){
        log.debug("initialize : запускается блок инициализации ...");

        createMenu();

        StartChogori.APP_MENU_CONTROLLER = this;

        //Создать поле поиска
        SEARCH_CONTAINER = hbSearch;
        PANE_WITH_SEARCH = createSearchField();

        SideChat sideChat = new SideChat();

        btnChat.setText("чат");
        btnChat.setGraphic(new ImageView(BtnImages.CHAT_WHITE_IMG));
        btnChat.setOnAction(event -> {
            open = !open;
            if(open) {
                SP_CHAT.setPrefWidth(CHAT_WIDTH);
                SP_CHAT.setMinWidth(CHAT_WIDTH);
                SP_CHAT.setMaxWidth(CHAT_WIDTH);
                SP_CHAT.getChildren().add(sideChat.getChatVBox());
            } else {
                for (double width = SP_CHAT.getWidth(); width >= 0; width--) {
                    SP_CHAT.setPrefWidth(width);
                    SP_CHAT.setMinWidth(width);
                    SP_CHAT.setMaxWidth(width);
                }
                SP_CHAT.getChildren().clear();
            }
        });

        btnCloseTab.setGraphic(new ImageView(BTN_CLOSE_WHITE_IMG));
        btnCloseTab.visibleProperty().bind(CH_TAB_PANE.getEmpty());
        btnCloseTab.setOnAction(event->{
            CH_TAB_PANE.closeThisTab(event);
        });

        log.debug("initialize : блок инициализации успешно выполнен");
    }


    /**
     * СОЗДАТЬ МЕНЮ
     */
    private void createMenu() {
        log.debug("createMenu : запускается создание меню ...");

        menuBar.getMenus().add(createMainMenu());
        //Чертежи
        menuBar.getMenus().add(createDraftsMenu());
        //Изделия
        if(CH_CURRENT_USER_GROUP.isReadProductStructures())
            menuBar.getMenus().add(createEditorMenu());
        //Материалы
        if(CH_CURRENT_USER_GROUP.isReadMaterials())
            menuBar.getMenus().add(createMaterialsMenu());
        //Калькулятор
//        menuBar.getMenus().add(createCalculatorMenu());
        //Изделия
//        if(CH_CURRENT_USER_GROUP.isReadProductStructures())
//            menuBar.getMenus().add(createEditorMenu());
        //Админ
        if(CH_CURRENT_USER_GROUP.isAdministrate())
            menuBar.getMenus().add(createAdminMenu());
        //Помощь
        menuBar.getMenus().add(createHelpMenu());
        log.debug("createMenu : меню успешно создано");
    }

    /**
     * ПОЛЕ ПОИСКА в одной строке с главным меню
     */
    private HBox createSearchField() {

        log.debug("createSearchField : поле поиска создается ...");
        Button searchNowButton = new Button();
        searchNowButton.setText("");
        searchNowButton.setGraphic(new ImageView(BtnImages.BTN_SEARCH_IMG));
        searchNowButton.setOnAction(e->{
            Platform.runLater(()->{
                if(CH_SEARCH_FIELD.getEnteredText().startsWith(KOMPLEKT)) return;
                CH_SEARCH_FIELD.searchNow(true);
                CH_SEARCH_FIELD.getSearchedTableView().requestFocus();
            });
        });

        BtnDoublePro doublePro = new BtnDoublePro(true);
        Button btnPro = doublePro.create();
        doublePro.getStateProperty().bindBidirectional(SearchField.searchProProperty);
        doublePro.getStateProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                if(CH_SEARCH_FIELD.getEnteredText().startsWith(KOMPLEKT)) return;
                CH_SEARCH_FIELD.searchNow(true);
                CH_SEARCH_FIELD.getSearchedTableView().requestFocus();
            });
        });

        HBox hbox = new HBox();

        CH_SEARCH_FIELD = new SearchField();
        CH_SEARCH_FIELD.setPrefWidth(300);
        Button btnClean = new Button();
        btnClean.setOnAction((e)->{
            CH_SEARCH_FIELD.getEditor().setText("");
            CH_SEARCH_FIELD.requestFocus();
        });
        btnClean.setGraphic(new ImageView(BtnImages.BTN_CLEAN_IMG_W));
        hbox.getChildren().addAll(searchNowButton, CH_SEARCH_FIELD, btnPro, btnClean);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setSpacing(2);
        hbox.getStylesheets().add(getClass().getResource("/chogori-css/toolpane-dark.css").toString());

        log.debug("createSearchField : поле поиска успешно создано");

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
        updateData.setOnAction(e->updateData());

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/chat/chat.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Чат";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * ОБНОВИТЬ ДАННЫЕ
     */
    public static void updateData() {

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/password/changePassword.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/settings/settings.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/logging/changeHistory.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "История изменений";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true,  loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- КАРТОТЕКА
     */
    private void openFileCabinet(ActionEvent event) {

        Thread t = new Thread(new OpenPassportsEditorTask());
        t.setDaemon(true);
        t.start();
    }

    /**
     * -- ЧЕРТЕЖИ
     */
    public void openDrafts(Event event) {

        Thread t = new Thread(new OpenDraftsEditorTask());
        t.setDaemon(true);
        t.start();
    }



    //######################   МАТЕРИАЛЫ   ##########################

    /**
     * МЕНЮ МАТЕРИАЛОВ
     */
    private Menu createMaterialsMenu() {

        Menu materialsMenu = new Menu("Номенклатура");

        MenuItem catalogOfMaterialsItem = new MenuItem("Каталог материалов");
        catalogOfMaterialsItem.setOnAction(this::openCatalogOfMaterials);

        MenuItem densitiesItem = new MenuItem("Плотность материалов");
        densitiesItem.setOnAction(this::openDensities);

        MenuItem prefixesItem = new MenuItem("Префиксы");
        prefixesItem.setOnAction(this::openPrefixes);

        materialsMenu.getItems().addAll(catalogOfMaterialsItem, densitiesItem);
        materialsMenu.getItems().add(new SeparatorMenuItem());
        materialsMenu.getItems().add(prefixesItem);

        return materialsMenu;
    }

    /**
     * -- МАТЕРИАЛЫ
     */
    private void openCatalogOfMaterials(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/catalogOfMaterials/catalogOfMaterials.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Материалы";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- ПЛОТНОСТИ МАТЕРИАЛОВ
     */
    private void openDensities(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/densities/densities.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Плотность материалов";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true,  loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * -- ПРЕФИКСЫ
     */
    private void openPrefixes(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/prefixes/prefixes.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Префиксы";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true,  loader.getController());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/catalogOfProducts/catalogOfProducts.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Изделия";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, null);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/logging/logging.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Логирование";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, loader.getController());
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
            String tabName = "Пользователи";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, loader.getController());
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
            String tabName = "Группы Пользователей";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName,parent, true, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * -- КАТАЛОГ ПАПОК
     */
    void openCatalogOfFolders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/catalogOfFolders/catalogOfFolders.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "Пакеты";
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void makeTest(ActionEvent event){
        Room group = new Room();
        group.setName("#1#96");
        group.setCreator(CH_CURRENT_USER);

        Room newGroup = CH_ROOMS.save(group);
    }

    //########################   ПОМОЩЬ    ###########################

    /**
     * МЕНЮ ПОМОЩЬ
     */
    private Menu createHelpMenu() {

        Menu helpMenu = new Menu("Помощь");

        MenuItem downloadLastVersion = new MenuItem("Скачать последнюю версию");
        downloadLastVersion.setOnAction(this::downloadLastVersion);

        MenuItem openInstruction = new MenuItem("Открыть инструкцию");
        openInstruction.setOnAction(this::openInstruction);

        MenuItem helpVideosOnline = new MenuItem("Обучающее видео");
        helpVideosOnline.setOnAction(this::openHelpVideosOnline);

        MenuItem aboutItem = new MenuItem("О программе...");
        aboutItem.setOnAction(this::openAbout);

        MenuItem test = new MenuItem("ТЕСТ");
        test.setOnAction(this::makeTest);

        helpMenu.getItems().addAll(downloadLastVersion, openInstruction, helpVideosOnline, aboutItem);
//        helpMenu.getItems().add(test);

        return helpMenu;
    }

    /**
     * Метод открывает инструкцию по работе с тубусом
     * @param event
     */
    @SneakyThrows
    private void openInstruction(ActionEvent event) {
        File instruction = new File("//serverhp.ntcpik.com/ntcpik/BazaPIK/TUBUS - инструкция.pdf");
        Desktop.getDesktop().open(instruction);
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
