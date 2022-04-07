package ru.wert.datapik.chogori.application.drafts;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.winform.statics.WinformStatic.clearCash;


@Slf4j
public class DraftsEditorController implements SearchableTab, UpdatableTabController {


    @FXML
    private StackPane spDrafts;

    @FXML
    private StackPane spPreviewer;

    @FXML
    private StackPane spCatalog;

    @FXML
    private SplitPane sppVertical;

    @FXML
    private SplitPane sppHorizontal;

    private Draft_TableView draftsTable;
    private Draft_PatchController draftPatchController;
    private PreviewerPatchController previewerPatchController;
//    private Label lblDraftInfo;
    private Folder_TableView folderTableView;
    private ProductGroup_TreeView<Folder> productGroupsTreeView;

    @FXML
    void initialize() {

        createCatalogOfFolders(); //Каталог пакетов

        createPreviewer(); //Предпросмотр

        createDraftsTable(); //ЧЕРТЕЖИ

        folderTableView.setDraftTable(draftsTable);

    }


    /**
     * ПРЕДПРОСМОТРЩИК
     */
    private void createPreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(spPreviewer, sppHorizontal, sppVertical); //Предпросмотр
    }

    /**
     * таблица с ЧЕРТЕЖАМИ
     */
    private void createDraftsTable() {

        Draft_Patch draftPatch = new Draft_Patch().create();
        draftPatchController = draftPatch.getDraftPatchController();

        draftPatchController.initDraftsTableView(previewerPatchController, new Folder(), SelectionMode.MULTIPLE);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, false, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(true, true, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createHorizontalDividerButton(sppHorizontal, 0.8, 0.4));

        //Сообщаем Previewer ссылку на tableView
        previewerPatchController.setDraftsTableView(draftsTable);
        //Монтируем
        spDrafts.getChildren().add(draftPatch.getParent());

    }

    /**
     * Создаем каталог ИЗДЕЛИЙ
     */
    private void createCatalogOfFolders() {
        CatalogOfFoldersPatch catalogPatch = new CatalogOfFoldersPatch().create();
        //Подключаем слушатель
        folderTableView = catalogPatch.getFolderTableView();
        productGroupsTreeView = catalogPatch.getProductGroupsTreeView();

        folderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            new Thread(()->{
                try {
                    Thread.sleep(500);
                    Item selectedItem = folderTableView.getSelectionModel().getSelectedItem();
                    if(selectedItem == newValue) {
                        //Нажата клавиша Alt
                        boolean altBtnPressed = CH_KEYS_NOW_PRESSED.contains(KeyCode.ALT);
                        //Если выделяется папка
                        if (selectedItem instanceof Folder) {
                            //Если требуется нажатие Alt
                            if (folderTableView.getAltOnProperty().get()) {
                                if (altBtnPressed) {
                                    clearCash();
                                    Platform.runLater(()->updateListOfDrafts(selectedItem));
                                }
                            } else {
                                clearCash();
                                Platform.runLater(()->updateListOfDrafts(selectedItem));
                            }
                        }
                        //Состав папки раскрывается только при нажатой alt
                        if (selectedItem instanceof ProductGroup && altBtnPressed) {
                            draftPatchController.showSourceOfPassports(selectedItem);
                            List<ProductGroup> selectedGroups = folderTableView.findMultipleProductGroups((ProductGroup) selectedItem);
                            List<Folder> folders = new ArrayList<>();
                            for (ProductGroup pg : selectedGroups) {
                                folders.addAll(CH_QUICK_FOLDERS.findAllByGroupId(pg.getId()));
                            }
                            draftsTable.setTempSelectedFolders(folders);
                            Platform.runLater(()->draftsTable.updateView());
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        });

        catalogPatch.getFoldersButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));

        //Монтируем каталог в панель
        Parent cat = catalogPatch.getCatalogOfFoldersPatch();
        spCatalog.getChildren().add(cat);

    }

    private void updateListOfDrafts(Item newValue) {
        draftPatchController.showSourceOfPassports(newValue);
        draftsTable.setTempSelectedFolders(Collections.singletonList((Folder) newValue));
        draftsTable.setSearchedText(""); //обнуляем поисковую строку
        draftsTable.setModifyingItem(newValue);
        draftsTable.updateView();
    }

    @Override//SearchableTab
    public void tuneSearching() {
        Platform.runLater(()->draftsTable.requestFocus());
    }

    @Override
    public void updateTab() {
        productGroupsTreeView.updateView();
        folderTableView.updateVisibleLeafOfTableView(folderTableView.getUpwardRow().getValue());
        draftsTable.updateTableView();
    }
}
