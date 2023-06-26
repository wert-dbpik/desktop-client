package ru.wert.datapik.chogori.application.passports;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.chogori.common.components.BtnDouble;
import ru.wert.datapik.chogori.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.datapik.chogori.entities.drafts.Draft_TableView;
import ru.wert.datapik.chogori.entities.drafts.Draft_Patch;
import ru.wert.datapik.chogori.entities.drafts.Draft_PatchController;
import ru.wert.datapik.chogori.entities.folders.Folder_TableView;
import ru.wert.datapik.chogori.entities.passports.Passport_Patch;
import ru.wert.datapik.chogori.entities.passports.Passport_PatchController;
import ru.wert.datapik.chogori.entities.passports.Passport_TableView;
import ru.wert.datapik.chogori.previewer.PreviewerPatchController;

import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;
import static ru.wert.datapik.chogori.images.BtnImages.*;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_KEYS_NOW_PRESSED;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;


@Slf4j
public class PassportsEditorController implements SearchableTab, UpdatableTabController {


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

    private Label lblSourceOfPassports;

    private Passport_TableView passportsTable;
    private PreviewerPatchController previewerPatchController;
    private Draft_TableView draftsTable;
    private Draft_Patch draftPatch;
    private Draft_PatchController draftPatchController;
    private Passport_Patch passportsPatch;
    private CatalogOfFoldersPatch catalogPatch;

    private Folder_TableView folderTableView;
    private ProductGroup_TreeView<Folder> productGroupsTreeView;

    private ChangeListener<Item> folderTableSelectedItemChangeListener;


    @FXML
    void initialize() {

        loadStpPreviewer(); //Предпросмотр инициализируется до Чертежей!

        loadStackPaneDrafts(); //Чертежи

        loadStackPaneCatalog(); //Каталог

        loadStackPanePassports(); //Пасспорта

    }

    public void openPassportFromChat(Passport passport){

        Draft draft = CH_QUICK_DRAFTS.findByPassport(passport).get(0);

        Folder folder = draft.getFolder();
        ProductGroup group = draft.getFolder().getProductGroup();
//        folderTableView.getSelectionModel().selectedItemProperty().removeListener(folderTableSelectedItemChangeListener);
//        folderTableView.updateVisibleLeafOfTableView(group);
//        folderTableView.getSelectionModel().select(folder);


        updateListOfPassports(folder);
        Platform.runLater(() -> {
            passportsTable.getSelectionModel().select(passport);
            passportsTable.scrollTo(passport);
//            folderTableView.getSelectionModel().selectedItemProperty().addListener(folderTableSelectedItemChangeListener);
        });


    }

    private void loadStpPreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical, true); //Предпросмотр
    }


    private BtnDouble createCatalogOrTableButton(){
        BtnDouble btnCatalogOrTable = new BtnDouble(
                BTN_CATALOG_IMG, "Открыть каталог",
                BTN_TABLE_VIEW_IMG, "Открыть входящие чертежи",
                false);
        btnCatalogOrTable.setOnAction(e->{
            if(btnCatalogOrTable.getStateProperty().get()) {
                stpDrafts.getChildren().clear();
                Parent cat = catalogPatch.getCatalogOfFoldersPatch();
                stpDrafts.getChildren().add(0, cat);
            } else {
                stpDrafts.getChildren().clear();
                stpDrafts.getChildren().add(0, draftPatch.getParent());
            }
        });
        return btnCatalogOrTable;
    }

    /**
     * Создаем таблицу ЧЕРТЕЖЕЙ
     */
    private void loadStackPaneDrafts() {

        draftPatch = new Draft_Patch().create();

        draftPatchController = draftPatch.getDraftPatchController();
        draftPatchController.initDraftsTableView(previewerPatchController, new Passport(), SelectionMode.MULTIPLE);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, true, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(false, false, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));
        draftsTable.getAltOnProperty().set(false); //Иначе превью не будет срабатывать

        previewerPatchController.getLblCount().textProperty().bind(
                Bindings.convert(draftsTable.getPreparedList().sizeProperty()));

        //Для отображения чертежа по умолчанию
        draftPatch.connectWithPreviewer(draftsTable, previewerPatchController);

        stpDrafts.getChildren().add(draftPatch.getParent());

    }

    /**
     * Создаем таблицу ИДЕНТИФИКАТОРОВ
     */
    private void loadStackPanePassports() {

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
            new Thread(()->{
                try {
                    Thread.sleep(500);
                    if (newValue == passportsTable.getSelectionModel().getSelectedItem()) {
                        Platform.runLater(()->{
                            draftsTable.setModifyingItem(newValue);
                            draftsTable.updateView();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        passportsTable.setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.RIGHT)) draftsTable.getManipulator().goDraftsForward();
            else if(e.getCode().equals(KeyCode.LEFT)) draftsTable.getManipulator().goDraftsBackward();
        });

        stpPassports.getChildren().add(passportsPatch.getParent());

    }

    /**
     * Создаем каталог ИЗДЕЛИЙ
     */
    private void loadStackPaneCatalog() {
        catalogPatch = new CatalogOfFoldersPatch().create();

        productGroupsTreeView = catalogPatch.getProductGroupsTreeView();

        //Подключаем слушатель
        folderTableView = catalogPatch.getFolderTableView();

        folderTableSelectedItemChangeListener = (observable, oldValue, newValue) -> {
            if (newValue instanceof Folder) {
                if (folderTableView.getAltOnProperty().get()) {
                    if (CH_KEYS_NOW_PRESSED.contains(KeyCode.ALT))
                        updateListOfPassports(newValue);
                } else
                    updateListOfPassports(newValue);

            }
        };

        folderTableView.getSelectionModel().selectedItemProperty().addListener(folderTableSelectedItemChangeListener);


        folderTableView.setOnMouseClicked(e->{
            //Нажата правая клавиша мыши
            boolean primaryBtn = e.getButton().equals(MouseButton.PRIMARY);
            //Есть право редактировать чертежи
            boolean editRights = CH_CURRENT_USER_GROUP.isEditDrafts();

            if((editRights && primaryBtn && e.isAltDown()) || (!editRights && primaryBtn) ){
                Item selectedItem = folderTableView.getSelectionModel().getSelectedItem();
                if(selectedItem instanceof Folder){
                    if(folderTableView.getAltOnProperty().get()){
                        if(e.isAltDown())
                            updateListOfPassports(selectedItem);
                    } else
                        updateListOfPassports(selectedItem);
                }
                if((editRights && selectedItem instanceof ProductGroup) || (!editRights && selectedItem instanceof ProductGroup && e.isAltDown())){
                    passportsPatch.getPassportPatchController().showSourceOfPassports(selectedItem);
                    List<ProductGroup> selectedGroups = folderTableView.findMultipleProductGroups((ProductGroup) selectedItem);
                    List<Folder> folders = new ArrayList<>();
                    for(ProductGroup pg : selectedGroups){
                        folders.addAll(CH_QUICK_FOLDERS.findAllByGroupId(pg.getId()));
                    }
                    if(folders.isEmpty()) return;
                    passportsTable.setSelectedFolders(folders);
                    passportsTable.updateRoutineTableView(Collections.singletonList((Passport) selectedItem), false);
                }

            }
        });

        catalogPatch.getFoldersButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));

    }




    private void updateListOfPassports(Item newValue) {
        passportsPatch.getPassportPatchController().showSourceOfPassports(newValue);

        passportsTable.setSelectedFolders(Collections.singletonList((Folder) newValue));
        passportsTable.setSearchedText(""); //обнуляем поисковую строку
        passportsTable.setModifyingItem(newValue);
        passportsTable.updateView();
    }

    @Override//SearchableTab
    public void tuneSearching() {
        Platform.runLater(()->passportsTable.requestFocus());
        CH_SEARCH_FIELD.changeSearchedTableView(passportsTable, "КАРТОЧКА");
    }


    @Override
    public void updateTab() {
        productGroupsTreeView.updateView();
        folderTableView.updateVisibleLeafOfTableView(folderTableView.getUpwardRow().getValue());
        draftsTable.updateTableView();
        passportsTable.updateTableView();
    }
}
