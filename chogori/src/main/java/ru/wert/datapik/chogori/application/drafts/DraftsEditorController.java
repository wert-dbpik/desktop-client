package ru.wert.datapik.chogori.application.drafts;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.tabs.SearchablePane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.AppStatic.clearCash;


@Slf4j
public class DraftsEditorController implements SearchablePane{


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
        folderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue instanceof Folder) {
                if(folderTableView.getAltOnProperty().get()){
                    if(CH_KEYS_NOW_PRESSED.contains(KeyCode.ALT)) {
                        clearCash();
                        updateListOfDrafts(newValue);
                    }
                } else {
                    clearCash();
                    updateListOfDrafts(newValue);
                }

            }
        });

        folderTableView.setOnMouseClicked(e->{
            //Нажата левая клавиша мыши
            boolean primaryBtn = e.getButton().equals(MouseButton.PRIMARY);
            //Есть право редактировать чертежи
            boolean editRights = CH_CURRENT_USER_GROUP.isEditDrafts();

            //Обновляет список чертежей
            if((editRights && primaryBtn && e.isAltDown()) || (!editRights && primaryBtn) ){
                Item selectedItem = folderTableView.getSelectionModel().getSelectedItem();
                if(selectedItem instanceof Folder){
                    if(folderTableView.getAltOnProperty().get()){
                        if(e.isAltDown()) {
                            clearCash();
                            updateListOfDrafts(selectedItem);
                        }
                    } else {
                        clearCash();
                        updateListOfDrafts(selectedItem);
                    }
                }
                if((editRights && selectedItem instanceof ProductGroup) || (!editRights && selectedItem instanceof ProductGroup && e.isAltDown())){
                    draftPatchController.showSourceOfPassports(selectedItem);
                    List<ProductGroup> selectedGroups = folderTableView.findMultipleProductGroups((ProductGroup) selectedItem);
                    List<Folder> folders = new ArrayList<>();
                    for(ProductGroup pg : selectedGroups){
                        folders.addAll(CH_QUICK_FOLDERS.findAllByGroupId(pg.getId()));
                    }
                    draftsTable.setTempSelectedFolders(folders);
                    draftsTable.updateView();
                }

            }
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

    @Override//SearchablePane
    public void tuneSearching() {
        Platform.runLater(()->draftsTable.requestFocus());
    }

}
