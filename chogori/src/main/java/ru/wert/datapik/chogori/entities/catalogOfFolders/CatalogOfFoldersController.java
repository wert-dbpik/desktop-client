package ru.wert.datapik.chogori.entities.catalogOfFolders;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import ru.wert.datapik.client.interfaces.UpdatableTabController;
import ru.wert.datapik.chogori.common.components.*;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.common.utils.ClipboardUtils;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.chogori.entities.product_groups._ProductGroup_TreeViewPatch;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_FOLDERS;
import static ru.wert.datapik.chogori.images.BtnImages.BTN_SEARCH_BLACK_IMG;
import static ru.wert.datapik.chogori.images.BtnImages.BTN_SEARCH_IMG;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class CatalogOfFoldersController implements UpdatableTabController {
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

    @Getter private ProductGroup_TreeView<Folder> productGroupsTreeView;

    @Getter private ItemTableView<Item> folderTableView;



    @FXML
    void initialize() {

        ClipboardUtils.clear();

        //Создаем связанные между собой панели каталога и изделий
        boolean useContextMenu = false;
        if(CH_CURRENT_USER.getUserGroup().isEditDrafts()) useContextMenu = true;
        createCatalogForms(useContextMenu);

        productGroupsTreeView.setConnectedForm(folderTableView);
        //Создаем панели инструментов
        createFolders_ToolBar();
        createCatalog_ToolBar();
        createFolders_Label();
    }

    private void createFolders_Label() {
//        lblCurrentProductGroup.setStyle("-fx-text-fill: darkblue; -fx-font-style: oblique;");
        lblCurrentProductGroup.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: blue");
        ObjectProperty<TreeItem<ProductGroup>> upwardProperty = ((Folder_TableView)folderTableView).getUpwardRowProperty();
        String rootName = productGroupsTreeView.getRoot().getValue().getName();
        upwardProperty.addListener((observable) -> {
            if(folderTableView.isGlobalOff() && upwardProperty.get() != null) {
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

        productGroupsTreeView = catalogPatch.createProductTreeView(folderTableView);

        ((Folder_TableView)folderTableView).plugContextMenuAndFolderManipulators(productGroupsTreeView);

        productGroupsTreeView.setOnMouseClicked((e)->{
            if(e.getButton() == MouseButton.PRIMARY) {
                TreeItem<ProductGroup> selectedItem = productGroupsTreeView.getSelectionModel().getSelectedItem();
                if(selectedItem != null)
                    ((Folder_TableView) folderTableView).setUpwardRow(selectedItem);
                    ((Folder_TableView) folderTableView).updateVisibleLeafOfTableView(selectedItem.getValue());
            }
        });

        folderTableView.updateView();

        folderTableView.setMinHeight(0.0);

        vbCatalog.getChildren().add(productGroupsTreeView);
        spFolderTableView.getChildren().add(folderTableView);

    }


    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createCatalog_ToolBar(){
        //Кнопка "Свернуть каталог"
        Button btnCatalogRollUp = new BtnRollUp();
        btnCatalogRollUp.setOnAction((e)->{
            productGroupsTreeView.foldTreeView();
        });

        //Кнопка "Развернуть каталог"
        Button btnCatalogRollDown = new BtnRollDown();
        btnCatalogRollDown.setOnAction((e)->{
            productGroupsTreeView.unfoldTreeView();
        });


        //При клике правой кнопкой на надписи "КАТАЛОГ" открывается меню "Добавить" в корень
        lblCatalog.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.SECONDARY)) {
                productGroupsTreeView.getSelectionModel().clearSelection();
                productGroupsTreeView.getContextMenu().show(lblCatalog, Side.BOTTOM, 0.0, 5.0);
            }
        });

        catalogButtons.getChildren().addAll(btnCatalogRollUp, btnCatalogRollDown);
    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ для ТАБЛИЦЫ ИЗДЕЛИЙ
     */
    private void createFolders_ToolBar(){

        //Кнопка ALT_ON
        BtnDoubleAlt<Item> btnDoubleAlt = new BtnDoubleAlt<Item>(folderTableView, false);
        Button btnAltOn = btnDoubleAlt.create();
        folderTableView.getAltOnProperty().bindBidirectional(btnDoubleAlt.getStateProperty());

        //Устанавливаем начальное значение altOn в контроллере
        btnDoubleAlt.getStateProperty().set(false);

        //Кнопка переключения на поиск по КОМПЛЕКТАМ
        Button btnSearchFolder = new Button();
        btnSearchFolder.setId("patchButton");
        btnSearchFolder.setText("");
        btnSearchFolder.setGraphic(new ImageView(BTN_SEARCH_BLACK_IMG));
        btnSearchFolder.setOnAction(e->{
            CH_SEARCH_FIELD.changeSearchedTableView(folderTableView, "КОМПЛЕКТ ЧЕРТЕЖЕЙ");
        });

// ======   ИСКЛЮЧЕНА ЗА НЕНАДОБНОСТЬЮ
        //Кнопка переключения режимов GLOBE/CATALOG
//        BtnDoubleGlobeVsCatalog btnDoubleGlobeVsCatalog = new BtnDoubleGlobeVsCatalog(folderTableView, false);
//        Button btnGlobeVsCatalog = btnDoubleGlobeVsCatalog.create();
//        btnDoubleGlobeVsCatalog.getStateProperty().bindBidirectional(folderTableView.getGlobalOffProperty());

        //Устанавливаем начальное значение globalOn в контроллере
        //Значение получилось перевернутым true - показывает Каталог
//        btnDoubleGlobeVsCatalog.getStateProperty().set(true);
// ==================================================================================================================
        foldersButtons.getChildren().addAll(btnAltOn, btnSearchFolder);
    }


    @Override
    public void updateTab() {
        productGroupsTreeView.updateView();
        ((Folder_TableView)folderTableView).updateVisibleLeafOfTableView(((Folder_TableView)folderTableView).getUpwardRow().getValue());
    }
}
