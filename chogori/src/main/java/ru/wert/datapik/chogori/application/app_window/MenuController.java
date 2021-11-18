package ru.wert.datapik.chogori.application.app_window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.chogori.StartChogori;
import ru.wert.datapik.chogori.application.editor.ExcelChooser;
import ru.wert.datapik.chogori.application.editor.ExcelEditorNewController;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;

public class MenuController {

    public MenuController() {
        StartChogori.CH_MENU_CONTROLLER = this;
    }

    @FXML
    void quit(ActionEvent event) {

    }

    @FXML
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
            CH_TAB_PANE.createNewTab(chosenFile.getName(), parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openDrafts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/draftsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/drafts-dark.css").toString());
            CH_TAB_PANE.createNewTab("Чертежи", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openPassports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/passports/passportsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Картотека", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void openDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/details/detailsEditor.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Детали", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openCatalogOfProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfProducts/catalogOfProducts.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Изделия", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openCatalogOfMaterials(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfMaterials/catalogOfMaterials.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Материалы", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openCatalogOfFolders(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfFolders/catalogOfFolders.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Пакеты", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openAssembles(ActionEvent event) {

    }

    @FXML
    void openUsers(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/usersPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Пользователи", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void openUserGroups(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/users/userGroupsPermissions.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            CH_TAB_PANE.createNewTab("Группы Пользователей", parent, true, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void openAdminDrafts(ActionEvent event) {

    }

    @FXML
    void initialize() {


    }
}
