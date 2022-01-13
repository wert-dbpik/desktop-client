package ru.wert.datapik.utils.entities.catalogOfFolders;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.components.BtnRollDown;
import ru.wert.datapik.utils.common.components.BtnRollUp;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups._ProductGroup_TreeViewPatch;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class CatalogOfFoldersController {

    @FXML
    private VBox vbCatalog;

    @FXML
    private StackPane spFolderTableView;

    @FXML
    private HBox catalogButtons;

    @FXML
    private HBox foldersButtons;

    @FXML
    private Label lblCatalog;

    private ProductGroup_TreeView<ProductGroup> catalogTreeView;

    @Getter private ItemTableView<Item> folderTableView;



    @FXML
    void initialize() {

        //Создаем панели инструментов
        createCatalog_ToolBar();
        createFolders_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createCatalog_TreeView();
        createFolders_TableView();

//        catalogTreeView.setConnectedForm(folderTableView);

    }

    public HBox getCatalogButtons() {
        return catalogButtons;
    }

    public HBox getFoldersButtons() {
        return foldersButtons;
    }

    /**
     * дерево КАТАЛОГА
     */
    private void createCatalog_TreeView() {

        catalogTreeView = _ProductGroup_TreeViewPatch.create();

        vbCatalog.getChildren().add(catalogTreeView);

        catalogTreeView.setOnMouseClicked((e)->{
            if(e.getButton() == MouseButton.PRIMARY)
                folderTableView.updateTableView();
        });

    }

    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createFolders_TableView() {
        folderTableView = new Folder_TableView(catalogTreeView, "ПАКЕТЫ ИЗДЕЛИЙ");
        folderTableView.updateView();

        folderTableView.setMinHeight(0.0);

        spFolderTableView.getChildren().add(folderTableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createCatalog_ToolBar(){
        //Кнопка "Свернуть каталог"
        Button btnCatalogRollUp = new BtnRollUp();
        btnCatalogRollUp.setOnAction((e)->{
            catalogTreeView.foldTreeView();
        });

        //Кнопка "Развернуть каталог"
        Button btnCatalogRollDown = new BtnRollDown();
        btnCatalogRollDown.setOnAction((e)->{
            catalogTreeView.unfoldTreeView();
        });


        //При клике правой кнопкой на надписи "КАТАЛОГ" открывается меню "Добавить" в корень
        lblCatalog.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.SECONDARY)) {
                catalogTreeView.getSelectionModel().clearSelection();
                catalogTreeView.getContextMenu().show(lblCatalog, Side.BOTTOM, 0.0, 5.0);
            }
        });

        catalogButtons.getChildren().addAll(btnCatalogRollUp, btnCatalogRollDown);
    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ для ТАБЛИЦЫ ИЗДЕЛИЙ
     */
    private void createFolders_ToolBar(){
        Button btnFoldersGlobe = new Button();
        btnFoldersGlobe.setId("patchButton");
        btnFoldersGlobe.setGraphic(new ImageView(BTN_GLOBE_IMG));
        btnFoldersGlobe.setTooltip(new Tooltip("Показать все"));
        btnFoldersGlobe.setOnAction((e)->{

            Platform.runLater(()->{
                folderTableView.updateTableView();
                catalogTreeView.getSelectionModel().select(catalogTreeView.getRoot());
                folderTableView.getSelectionModel().select(0);
            });

        });

//        Button btnFoldersGlobe = new BtnGlobe(productTableView);

        foldersButtons.getChildren().addAll(btnFoldersGlobe);
    }


}
