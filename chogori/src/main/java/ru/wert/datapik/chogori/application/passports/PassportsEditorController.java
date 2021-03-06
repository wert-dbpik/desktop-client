package ru.wert.datapik.chogori.application.passports;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
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
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_KEYS_NOW_PRESSED;


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


    @FXML
    void initialize() {

        loadStpPreviewer(); //???????????????????????? ???????????????????????????????? ???? ????????????????!

        loadStackPaneDrafts(); //??????????????

        loadStackPaneCatalog(); //??????????????

        loadStackPanePassports(); //??????????????????

    }

    private void loadStpPreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical, true); //????????????????????????
    }


    private BtnDouble createCatalogOrTableButton(){
        BtnDouble btnCatalogOrTable = new BtnDouble(
                BTN_CATALOG_IMG, "?????????????? ??????????????",
                BTN_TABLE_VIEW_IMG, "?????????????? ???????????????? ??????????????",
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
     * ?????????????? ?????????????? ????????????????
     */
    private void loadStackPaneDrafts() {

        draftPatch = new Draft_Patch().create();

        draftPatchController = draftPatch.getDraftPatchController();
        draftPatchController.initDraftsTableView(previewerPatchController, new Passport(), SelectionMode.MULTIPLE);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, true, true, true, false,
                false, true);
        //???????????????????????????????? ???????????? ???????????????????? ?? ?????????????????? ??????????????
        draftPatchController.initDraftsToolBar(false, false, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));
        draftsTable.getAltOnProperty().set(false); //?????????? ???????????? ???? ?????????? ??????????????????????

        previewerPatchController.getLblCount().textProperty().bind(
                Bindings.convert(draftsTable.getPreparedList().sizeProperty()));

        //?????? ?????????????????????? ?????????????? ???? ??????????????????
        draftPatch.connectWithPreviewer(draftsTable, previewerPatchController);

        stpDrafts.getChildren().add(draftPatch.getParent());

    }

    /**
     * ?????????????? ?????????????? ??????????????????????????????
     */
    private void loadStackPanePassports() {

        passportsPatch = new Passport_Patch().create();

        Passport_PatchController passportPatchController = passportsPatch.getPassportPatchController();
        passportPatchController.initPassportsTableView(previewerPatchController, new Passport(), SelectionMode.SINGLE, false);
        passportsTable = passportPatchController.getPassportsTable();
        passportsTable.showTableColumns(false, false);
        passportsTable.setModifyingClass(new Folder());
        //???????????????????????????????? ???????????? ???????????????????? ?? ?????????????????? ??????????????
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
     * ?????????????? ?????????????? ??????????????
     */
    private void loadStackPaneCatalog() {
        catalogPatch = new CatalogOfFoldersPatch().create();

        productGroupsTreeView = catalogPatch.getProductGroupsTreeView();

        //???????????????????? ??????????????????
        folderTableView = catalogPatch.getFolderTableView();

        folderTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue instanceof Folder) {
                if(folderTableView.getAltOnProperty().get()){
                    if(CH_KEYS_NOW_PRESSED.contains(KeyCode.ALT))
                        updateListOfPassports(newValue);
                } else
                    updateListOfPassports(newValue);

            }
        });

        folderTableView.setOnMouseClicked(e->{
            //???????????? ???????????? ?????????????? ????????
            boolean primaryBtn = e.getButton().equals(MouseButton.PRIMARY);
            //???????? ?????????? ?????????????????????????? ??????????????
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
                    passportsTable.updateRoutineTableView();
                }

            }
        });

        catalogPatch.getFoldersButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.4));

    }




    private void updateListOfPassports(Item newValue) {
        passportsPatch.getPassportPatchController().showSourceOfPassports(newValue);

        passportsTable.setSelectedFolders(Collections.singletonList((Folder) newValue));
        passportsTable.setSearchedText(""); //???????????????? ?????????????????? ????????????
        passportsTable.setModifyingItem(newValue);
        passportsTable.updateView();
    }

    @Override//SearchableTab
    public void tuneSearching() {
        Platform.runLater(()->passportsTable.requestFocus());
    }


    @Override
    public void updateTab() {
        productGroupsTreeView.updateView();
        folderTableView.updateVisibleLeafOfTableView(folderTableView.getUpwardRow().getValue());
        draftsTable.updateTableView();
        passportsTable.updateTableView();
    }
}
