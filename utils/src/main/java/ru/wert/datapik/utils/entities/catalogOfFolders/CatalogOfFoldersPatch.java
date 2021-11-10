package ru.wert.datapik.utils.entities.catalogOfFolders;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;

import java.io.IOException;

public class CatalogOfFoldersPatch {

    private CatalogOfFoldersController catalogOfFoldersController;
    private Parent catalogOfFoldersPatch;

    public CatalogOfFoldersPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfFolders/catalogOfFolders.fxml"));
            catalogOfFoldersPatch = loader.load();
            catalogOfFoldersController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public CatalogOfFoldersController getCatalogOfFoldersController() {
        return catalogOfFoldersController;
    }

    public Parent getCatalogOfFoldersPatch() {
        return catalogOfFoldersPatch;
    }

    public Folder_TableView getFolderTableView() {
        return (Folder_TableView) catalogOfFoldersController.getFolderTableView();
    }

    public HBox getCatalogButtons() {
        return catalogOfFoldersController.getCatalogButtons();
    }

    public HBox getFoldersButtons() {
        return catalogOfFoldersController.getFoldersButtons();
    }
}
