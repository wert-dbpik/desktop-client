package ru.wert.datapik.chogori.application.passports;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.utils.tabs.SearchablePane;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.components.BtnDouble;
import ru.wert.datapik.utils.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;
import ru.wert.datapik.utils.entities.passports.Passport_Patch;
import ru.wert.datapik.utils.entities.passports.Passport_PatchController;
import ru.wert.datapik.utils.entities.passports.Passport_TableView;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.Comparators;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.*;
import static ru.wert.datapik.utils.toolpane.ChogoriToolBar.*;


@Slf4j
public class PassportsEditorController implements SearchablePane {


    @FXML
    private SplitPane sppHorizontal;

    @FXML
    private SplitPane sppVertical;

    @FXML
    private StackPane stpPassports;

    @FXML
    private StackPane stpPreviewer;

    @FXML
    private StackPane stpDrafts;

    private Passport_TableView passportsTable;
    private PreviewerPatchController previewerPatchController;
    private Draft_TableView draftsTable;
    private Draft_Patch draftPatch;
    private Passport_Patch passportsPatch;
    private CatalogOfFoldersPatch catalogPatch;


    @FXML
    void initialize() {

        loadStpPreviewer(); //Предпросмотр инициализируется до Чертежей!

        loadStpDrafts(); //Чертежи

        loadStpCatalog(); //Каталог

        loadStpPassports(); //Пасспорта

    }

    private void loadStpPreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical); //Предпросмотр
    }


    private BtnDouble createCatalogOrTableButton(){
        BtnDouble btnCatalogOrTable = new BtnDouble(
                BTN_CATALOG_IMG, "Открыть каталог",
                BTN_TABLE_VIEW_IMG, "Открыть входящие чертежи");
        btnCatalogOrTable.setOnAction(e->{
            if(btnCatalogOrTable.getLogicProperty()) {
                stpDrafts.getChildren().clear();
                stpDrafts.getChildren().add(0, draftPatch.getParent());
            } else {
                stpDrafts.getChildren().clear();
                Parent cat = catalogPatch.getCatalogOfFoldersPatch();
                stpDrafts.getChildren().add(0, cat);
            }
        });
        return btnCatalogOrTable;
    }

    /**
     * Создаем таблицу ЧЕРТЕЖЕЙ
     */
    private void loadStpDrafts() {

        draftPatch = new Draft_Patch().create();
        Draft_PatchController draftPatchController = draftPatch.getDraftPatchController();
        draftPatchController.initDraftsTableView(previewerPatchController, new Passport(), SelectionMode.SINGLE, false);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, true, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(false, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));

        //Для отображения чертежа
        draftsTable.getPreparedList().addListener((observable, oldValue, newValue) -> {
            List<Draft> drafts = new ArrayList<>(newValue);
            if (!drafts.isEmpty()) {
                drafts.sort(Comparators.draftsForPreviewerComparator());
                AppStatic.openDraftInPreviewer(drafts.get(0), previewerPatchController);
            } else {
                //Отображаем NO_IMAGE
                AppStatic.openDraftInPreviewer(null, previewerPatchController);
            }
        });

        stpDrafts.getChildren().add(draftPatch.getParent());

    }

    /**
     * Создаем таблицу ИДЕНТИФИКАТОРОВ
     */
    private void loadStpPassports() {

        passportsPatch = new Passport_Patch().create();
        Passport_PatchController passportPatchController = passportsPatch.getPassportPatchController();
        passportPatchController.initPassportsTableView(previewerPatchController, new Passport(), SelectionMode.SINGLE, false);
        passportsTable = passportPatchController.getPassportsTable();
        passportsTable.showTableColumns(false, false);
        passportsTable.setModifyingClass(new Folder());
        //Инструментальную панель инициируем в последнюю очередь
        passportPatchController.initPassportsToolBar(true, true);
        passportPatchController.getHboxPassportsButtons().getChildren().addAll(createCatalogOrTableButton(),
                CommonUnits.createHorizontalDividerButton(sppHorizontal, 0.8, 0.4));

        passportsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                draftsTable.setModifyingItem(newValue);
                draftsTable.updateView();
            });
        });

        stpPassports.getChildren().add(passportsPatch.getParent());

    }

    /**
     * Создаем каталог ИЗДЕЛИЙ
     */
    private void loadStpCatalog() {
        catalogPatch = new CatalogOfFoldersPatch().create();

        //Подключаем слушатель
        Folder_TableView folderTableView = catalogPatch.getFolderTableView();
        folderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            passportsTable.setSearchedText(""); //обнуляем поисковую строку
            passportsTable.setModifyingItem(newValue);
            passportsTable.updateView();
        });

        catalogPatch.getFoldersButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));


    }

    @Override
    public void tuneSearching() {
        CH_SEARCH_FIELD.setSearchableTableController(passportsTable);
        String searchedText = passportsTable.getSearchedText();
        if (searchedText.equals(""))
            CH_SEARCH_FIELD.setPromptText("ДЕЦИМАЛЬНЫЙ НОМЕР");
        else
            CH_SEARCH_FIELD.setSearchedText(passportsTable.getSearchedText());
    }


}
