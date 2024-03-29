package ru.wert.tubus.chogori.entities.catalogOfFolders;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.chogori.entities.folders.Folder_TableView;
import ru.wert.tubus.chogori.entities.product_groups.ProductGroup_TreeView;

import java.io.IOException;

public class CatalogOfFoldersPatch {

    private CatalogOfFoldersController catalogOfFoldersController;
    private Parent catalogOfFoldersPatch;

    public CatalogOfFoldersPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/catalogOfFolders/catalogOfFolders.fxml"));
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

    public ProductGroup_TreeView<Folder> getProductGroupsTreeView() {
        return catalogOfFoldersController.getProductGroupsTreeView();
    }

    public HBox getCatalogButtons() {
        return catalogOfFoldersController.getCatalogButtons();
    }

    public HBox getFoldersButtons() {
        return catalogOfFoldersController.getFoldersButtons();
    }
}
