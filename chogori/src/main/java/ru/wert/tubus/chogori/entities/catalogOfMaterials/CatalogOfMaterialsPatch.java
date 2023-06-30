package ru.wert.tubus.chogori.entities.catalogOfMaterials;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import ru.wert.tubus.chogori.entities.materials.Material_TableView;

import java.io.IOException;

public class CatalogOfMaterialsPatch {


    @Getter private CatalogOfMaterialsController catalogOfMaterialsController;
    @Getter private Parent catalogOfMaterialsPatch;

    public CatalogOfMaterialsPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/catalogOfMaterials/catalogOfMaterials.fxml"));
            catalogOfMaterialsPatch = loader.load();
            catalogOfMaterialsController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Material_TableView getMaterialTableView() {
        return (Material_TableView) catalogOfMaterialsController.getMaterialTableView();
    }

    public HBox getCatalogButtons() {
        return catalogOfMaterialsController.getCatalogButtons();
    }

    public HBox getMaterialsButtons() {
        return catalogOfMaterialsController.getMaterialsButtons();
    }
}
