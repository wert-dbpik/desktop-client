package ru.wert.datapik.chogori.application.editor;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.components.BtnDouble;
import ru.wert.datapik.utils.editor.table.Excel_Patch;
import ru.wert.datapik.utils.editor.table.Excel_PatchController;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.info.InfoPatch;
import ru.wert.datapik.utils.info.InfoPatchController;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.Comparators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.BTN_CATALOG_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_TABLE_VIEW_IMG;

/**
 * Класс описывает контроллер редактора таблиц Excel
 */
public class ExcelEditorNewController {


    @FXML
    private SplitPane sppHorizontal;

    @FXML
    private SplitPane sppVertical;

    @FXML
    private StackPane stpExcel;

    @FXML
    private StackPane stpInfo;

    @FXML
    private StackPane stpPreviewer;

    /**
     * Патч с предпросмотром
     */
    private PreviewerPatchController previewerPatchController;

    /**
     * Патч с таблицей чертежей
     */
    private Draft_Patch draftPatch;
    /**
     * Таблица с чертежами
     */
    private Draft_TableView draftsTable;

    /**
     * Патч с доступной информацией
     */
    private InfoPatch infoPatch;
    /**
     * Контроллер патча с доступной информацией
     */
    private InfoPatchController infoPatchController;
    /**
     * Контроллер патча с таблицей excel файла
     */
    private Excel_PatchController excelPatchController;
    /**
     * Путь отображаемого excel файла
     */
    private File excelFile;

    public void init(File excelFile){
        this.excelFile = excelFile;

        loadStpInfo();
        loadStpDrafts();
        loadStpPreviewer();
        loadStpExcel();
    }

    /**
     * Создание левой панели с excel таблицей
     */
    private void loadStpExcel() {

        Excel_Patch excelPatch = new Excel_Patch().create();
        excelPatchController = excelPatch.getExcelPatchController();
        excelPatchController.initExcelTableView(excelFile, previewerPatchController, true);
        excelPatchController.initExcelToolBar(true, true);

        stpExcel.getChildren().add(excelPatch.getParent());

    }

    /**
     * Создание панели с таблицей доступных чертежей
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
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical));

        //Для отображения чертежа
        draftsTable.getPreparedList().addListener((observable, oldValue, newValue) -> {
            List<Draft> drafts = new ArrayList<>(newValue);
            if (!drafts.isEmpty()) {
                drafts.sort(Comparators.draftsForPreviewerComparator());
                AppStatic.openDraftInPreviewer(drafts.get(0), previewerPatchController);
            } else {
                AppStatic.openDraftInPreviewer(null, previewerPatchController);
            }
        });

        stpInfo.getChildren().add(draftPatch.getParent());

    }


    /**
     * Создание правую панель с информацией
     */
    private void loadStpInfo() {
    }

    /**
     * Создание предпросмотрщика
     */
    private void loadStpPreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical); //Предпросмотр
    }

    private BtnDouble createInfoOrDraftsTableButton(){
        BtnDouble btnCatalogOrTable = new BtnDouble(
                BTN_CATALOG_IMG, "Показать информацию",
                BTN_TABLE_VIEW_IMG, "Показать чертежи");
        btnCatalogOrTable.setOnAction(e->{
            if(btnCatalogOrTable.getLogicProperty()) {
                stpInfo.getChildren().clear();
                stpInfo.getChildren().add(0, draftPatch.getParent());
            } else {
                stpInfo.getChildren().clear();
                Parent cat = infoPatch.getParent();
                stpInfo.getChildren().add(0, cat);
            }
        });
        return btnCatalogOrTable;
    }

}
