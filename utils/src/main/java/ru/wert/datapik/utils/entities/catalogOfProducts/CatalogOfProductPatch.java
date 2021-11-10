package ru.wert.datapik.utils.entities.catalogOfProducts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import ru.wert.datapik.utils.entities.products.Product_TableView;

import java.io.IOException;

public class CatalogOfProductPatch {

    private CatalogOfProductsController catalogOfProductsController;
    private Parent catalogOfProductsPatch;

    public CatalogOfProductPatch() {

    }

    public CatalogOfProductPatch create() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/catalogOfProducts/catalogOfProducts.fxml"));
            catalogOfProductsPatch = loader.load();
            catalogOfProductsController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public CatalogOfProductsController getCatalogOfProductsController() {
        return catalogOfProductsController;
    }

    public Parent getCatalogOfProductsPatch() {
        return catalogOfProductsPatch;
    }

    public Product_TableView getProductTableView() {
        return (Product_TableView) catalogOfProductsController.getProductTableView();
    }

    public HBox getCatalogButtons(){
        return catalogOfProductsController.getCatalogButtons();
    }

    public HBox getProductsButtons(){
        return catalogOfProductsController.getProductsButtons();
    }
}
