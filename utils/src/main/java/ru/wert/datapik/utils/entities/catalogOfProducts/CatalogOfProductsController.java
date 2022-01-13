package ru.wert.datapik.utils.entities.catalogOfProducts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.entities.products.Product_TableView;
import ru.wert.datapik.utils.entities.product_groups._ProductGroup_TreeViewPatch;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class CatalogOfProductsController {

    @FXML
    private VBox vbCatalog;

    @FXML
    private StackPane spProductTableView;

    @FXML
    private HBox catalogButtons;

    @FXML
    private HBox productsButtons;

    private Item_TreeView<Product, ProductGroup> catalogTreeView;

    @Getter private CatalogTableView<Product, ProductGroup> productTableView;



    @FXML
    void initialize() {

        //Создаем панели инструментов
        createCatalog_ToolBar();
        createProducts_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createCatalog_TreeView();
        createProducts_TableView();



        catalogTreeView.setConnectedForm(productTableView);

    }

    public HBox getCatalogButtons() {
        return catalogButtons;
    }

    public HBox getProductsButtons() {
        return productsButtons;
    }

    /**
     * дерево КАТАЛОГА
     */
    private void createCatalog_TreeView() {
        catalogTreeView = _ProductGroup_TreeViewPatch.create();

        vbCatalog.getChildren().add(catalogTreeView);

//        catalogTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newGroup) -> {
//            productTableView.updateCatalogView(newGroup, false);
//        });

        catalogTreeView.setOnMouseClicked((e)->{
            if(e.getButton() == MouseButton.PRIMARY)
                productTableView.updateTableView();
        });

    }

    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createProducts_TableView() {
        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditProductStructures();
        productTableView = new Product_TableView(catalogTreeView, "ИЗДЕЛИЯ", useContextMenu);
        productTableView.updateView();

        productTableView.setMinHeight(0.0);

        spProductTableView.getChildren().add(productTableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createCatalog_ToolBar(){
        Button btnCatalogRollUp = new Button();
        btnCatalogRollUp.setGraphic(new ImageView(BTN_ROLLUP_IMG));
        btnCatalogRollUp.setTooltip(new Tooltip("Свернуть каталог"));
        btnCatalogRollUp.setOnAction((e)->{
            catalogTreeView.foldTreeView();
        });

        Button btnCatalogRollDown = new Button();
        btnCatalogRollDown.setGraphic(new ImageView(BTN_ROLLDOWN_IMG));
        btnCatalogRollDown.setTooltip(new Tooltip("Развернуть каталог"));
        btnCatalogRollDown.setOnAction((e)->{
            catalogTreeView.unfoldTreeView();
        });

        catalogButtons.getChildren().addAll(btnCatalogRollUp, btnCatalogRollDown);
    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ для ТАБЛИЦЫ ИЗДЕЛИЙ
     */
    private void createProducts_ToolBar(){
        Button btnProductsGlobe = new Button();
        btnProductsGlobe.setGraphic(new ImageView(BTN_GLOBE_IMG));
        btnProductsGlobe.setTooltip(new Tooltip("Показать все"));
        btnProductsGlobe.setOnAction((e)->{

            Platform.runLater(()->{
                productTableView.updateCatalogView(catalogTreeView.getRoot(), true);
                catalogTreeView.getSelectionModel().select(catalogTreeView.getRoot());
                productTableView.getSelectionModel().select(0);
            });

        });

//        Button btnProductsGlobe = new BtnGlobe(productTableView);

        productsButtons.getChildren().addAll(btnProductsGlobe);
    }


}
