package ru.wert.datapik.utils.entities.catalogOfFolders;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogableItem;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.components.*;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups._ProductGroup_TreeViewPatch;
import ru.wert.datapik.utils.search.Searchable;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

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

    @FXML
    private Label lblSetOfDrafts;

    private ProductGroup_TreeView<Folder> catalogTreeView;

    @Getter private ItemTableView<Item> folderTableView;



    @FXML
    void initialize() {

        ClipboardUtils.clear();

        //Создаем панели инструментов
        createCatalog_ToolBar();


        //Создаем связанные между собой панели каталога и изделий
        boolean useContextMenu = false;
        if(CH_CURRENT_USER.getUserGroup().isEditDrafts()) useContextMenu = true;
        createCatalogForms(useContextMenu);

        catalogTreeView.setConnectedForm(folderTableView);
        createFolders_ToolBar();



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
    private void createCatalogForms(boolean useContextMenu) {

        _ProductGroup_TreeViewPatch<Folder> catalogPatch = new _ProductGroup_TreeViewPatch<>();
        catalogPatch.setDependedItemService(CH_FOLDERS);

        folderTableView = new Folder_TableView("ПАКЕТЫ ИЗДЕЛИЙ", useContextMenu);

        catalogTreeView = catalogPatch.createProductTreeView(folderTableView);

        ((Folder_TableView)folderTableView).plugContextMenuAndFolderManipulators(catalogTreeView);

        catalogTreeView.setOnMouseClicked((e)->{
            if(e.getButton() == MouseButton.PRIMARY) {
                if(catalogTreeView.getSelectionModel().getSelectedItem() != null)
                    folderTableView.updateTableView();
            }
        });

        folderTableView.updateView();

        folderTableView.setMinHeight(0.0);

        vbCatalog.getChildren().add(catalogTreeView);
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

        Button btnDoubleGlobeVsCatalog = new BtnDoubleGlobeVsCatalog(folderTableView).create();

        foldersButtons.getChildren().addAll(btnDoubleGlobeVsCatalog);
    }


}
