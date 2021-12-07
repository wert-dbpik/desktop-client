package ru.wert.datapik.chogori.application.drafts;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.tabs.SearchablePane;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;


@Slf4j
public class DraftsEditorController implements SearchablePane {


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
    private PreviewerPatchController previewerController;
    private Label lblDraftInfo;

    @FXML
    void initialize() {

        createCatalogOfFolders(); //Каталог пакетов

        createPreviewer(); //Предпросмотр

        createDraftsTable(); //ЧЕРТЕЖИ

    }


    /**
     * ПРЕДПРОСМОТРЩИК
     */
    private void createPreviewer() {

        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        previewerController = previewerPatch.getController();
        previewerController.initPreviewer(CH_PDF_VIEWER, CH_MAIN_STAGE.getScene());
        previewerController.initPreviewerToolBar(true);
        previewerController.getHboxPreviewerButtons().getChildren().add(CommonUnits.createExpandPreviewButton(sppHorizontal, sppVertical));
        lblDraftInfo = previewerPatch.getLblDraftInfo();

        spPreviewer.getChildren().add(previewerPatch.getParent());

    }

    /**
     * таблица с ЧЕРТЕЖАМИ
     */
    private void createDraftsTable() {

        Draft_Patch draftPatch = new Draft_Patch().create();
        Draft_PatchController draftPatchController = draftPatch.getDraftPatchController();

        draftPatchController.initDraftsTableView(previewerController, new Folder(), SelectionMode.MULTIPLE, true);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, false, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(true, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createHorizontalDividerButton(sppHorizontal, 0.8, 0.4));

        //Сообщаем Previewer ссылку на tableView
        previewerController.setDraftsTableView(draftsTable);
        //Монтируем
        spDrafts.getChildren().add(draftPatch.getParent());

    }

    /**
     * Создаем каталог ИЗДЕЛИЙ
     */
    private void createCatalogOfFolders() {
        CatalogOfFoldersPatch catalogPatch = new CatalogOfFoldersPatch().create();

        //Подключаем слушатель
        Folder_TableView folderTableView = catalogPatch.getFolderTableView();
        folderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            draftsTable.setSearchedText(""); //обнуляем поисковую строку
            draftsTable.setModifyingItem(newValue);
            draftsTable.updateView();
        });

        catalogPatch.getFoldersButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));


        //Монтируем каталог в панель
        Parent cat = catalogPatch.getCatalogOfFoldersPatch();
        spCatalog.getChildren().add(cat);

    }

    @Override//SearchablePane
    public void tuneSearching() {
        CH_SEARCH_FIELD.setSearchableTableController(draftsTable);
        String searchedText = draftsTable.getSearchedText();
        CH_SEARCH_FIELD.setSearchedText(searchedText);
        if (searchedText.equals(""))
            CH_SEARCH_FIELD.setPromptText("ЧЕРТЕЖ");
        else
            CH_SEARCH_FIELD.setSearchedText(draftsTable.getSearchedText());
    }

}
