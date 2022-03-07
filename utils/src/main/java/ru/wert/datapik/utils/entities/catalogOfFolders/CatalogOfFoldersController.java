package ru.wert.datapik.utils.entities.catalogOfFolders;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.components.*;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups._ProductGroup_TreeViewPatch;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_FOLDERS;
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

    @FXML
    private Label lblCurrentProductGroup;

    private ProductGroup_TreeView<Folder> catalogTreeView;

    @Getter private ItemTableView<Item> folderTableView;



    @FXML
    void initialize() {

        ClipboardUtils.clear();

        //Создаем связанные между собой панели каталога и изделий
        boolean useContextMenu = false;
        if(CH_CURRENT_USER.getUserGroup().isEditDrafts()) useContextMenu = true;
        createCatalogForms(useContextMenu);

        catalogTreeView.setConnectedForm(folderTableView);
        //Создаем панели инструментов
        createFolders_ToolBar();
        createCatalog_ToolBar();
        createFolders_Label();
    }

    private void createFolders_Label() {
        lblCurrentProductGroup.setStyle("-fx-text-fill: darkblue; -fx-font-style: oblique;");
        ObjectProperty<TreeItem<ProductGroup>> upwardProperty = ((Folder_TableView)folderTableView).getUpwardRowProperty();
        String rootName = catalogTreeView.getRoot().getValue().getName();
        upwardProperty.addListener((observable) -> {
            if(folderTableView.isGlobalOn() && upwardProperty.get() != null) {
                StringBuilder sb = new StringBuilder("...");
                TreeItem<ProductGroup> lastParent = upwardProperty.get(); // = newValue
                while (!lastParent.getValue().getName().equals(rootName)) {
                    sb.insert(0, lastParent.getValue().getName() + "/");
                    lastParent = lastParent.getParent();
                }
                lblCurrentProductGroup.setText(sb.toString());
            }
        });
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

        BtnDoubleAlt<Item> btnDoubleAlt = new BtnDoubleAlt<Item>(folderTableView, false);
        Button btnAltOn = btnDoubleAlt.create();
        folderTableView.getAltOnProperty().bindBidirectional(btnDoubleAlt.getStateProperty());

        //Устанавливаем начальное значение
        btnDoubleAlt.getStateProperty().set(true);

        BtnDoubleGlobeVsCatalog btnDouble = new BtnDoubleGlobeVsCatalog(folderTableView, false);
        Button btnGlobeVsCatalog = btnDouble.create();
        btnDouble.getStateProperty().bindBidirectional(folderTableView.getGlobalOnProperty());

        btnDouble.getStateProperty().set(true);

        foldersButtons.getChildren().addAll(btnAltOn, btnGlobeVsCatalog);
    }


}
