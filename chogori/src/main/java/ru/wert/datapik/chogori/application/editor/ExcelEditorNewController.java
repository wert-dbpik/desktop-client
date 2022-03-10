package ru.wert.datapik.chogori.application.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.chogori.application.common.CommonUnits;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.utils.common.components.BtnDouble;
import ru.wert.datapik.utils.editor.model.EditorRow;
import ru.wert.datapik.utils.editor.table.Excel_Patch;
import ru.wert.datapik.utils.editor.table.Excel_PatchController;
import ru.wert.datapik.utils.editor.table.Excel_TableView;
import ru.wert.datapik.utils.entities.drafts.Draft_Patch;
import ru.wert.datapik.utils.entities.drafts.Draft_PatchController;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.info.InfoPatch;
import ru.wert.datapik.utils.info.InfoPatchController;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.Comparators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

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
    private Excel_TableView excelTable;
    private StackPane infoStackPane;
    private PreviewerPatchController previewerController;

    public void init(File excelFile){
        this.excelFile = excelFile;

//        createInfoOrDraftsTableButton();
        loadStackPaneInfo();
        loadStackPaneDrafts();
        loadStackPanePreviewer();
        loadStackPaneExcel();

    }

    /**
     * Создание левой панели с excel таблицей
     */
    private void loadStackPaneExcel() {

        Excel_Patch excelPatch = new Excel_Patch().create();
        excelPatchController = excelPatch.getExcelPatchController();
        excelPatchController.initExcelTableView(excelFile, previewerPatchController, true);
        excelPatchController.initExcelToolBar(true, true);
        //Добавляем кнопки на панель
        excelPatchController.getHbButtons().getChildren().add(createInfoOrDraftsTableButton());
        excelPatchController.getHbButtons().getChildren().add(CommonUnits.createHorizontalDividerButton(sppHorizontal, 0.8, 0.4));
        //Наименование файла
        excelPatchController.getLblExcelFile().setText(excelFile.getName());
        excelTable = excelPatchController.getExcelTable();

        setIndividualSettingsOfExcelTable();


        stpExcel.getChildren().add(excelPatch.getParent());

    }

    private void setIndividualSettingsOfExcelTable(){
        excelTable.getSelectionModel().setCellSelectionEnabled(false);
        excelTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Prefix prefix = CH_QUICK_PREFIXES.findByName("ПИК");
        excelTable.getRowNumber().setVisible(false);
        excelTable.getKrp().setVisible(false);
        excelTable.getMaterial().setVisible(false);
        excelTable.getParamA().setVisible(false);
        excelTable.getParamB().setVisible(false);

        ((TableView<EditorRow>)excelTable).getSelectionModel().selectedItemProperty().addListener(observable ->{
            EditorRow selectedRow = excelTable.getSelectionModel().getSelectedItem();
            String number = selectedRow.getDecNumber();

            Pattern p = Pattern.compile("\\d{3}.?\\d{3}\\.\\d{3}"); //Децимальный номер xxxxxx.xxx
            Matcher m = p.matcher(number);
            String decNumber = "";
            while(m.find()){
                decNumber = number.substring(m.start(), m.end());
            }

            Passport passport = CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(prefix,decNumber);

//            List<Draft> drafts = CH_QUICK_DRAFTS.findByPassport(passport);
            Platform.runLater(()->{
                draftsTable.setModifyingItem(passport);
                draftsTable.updateView();
            });
        });
    }

    /**
     * Создание панели с таблицей доступных чертежей
     */
    private void loadStackPaneDrafts() {

        draftPatch = new Draft_Patch().create();
        Draft_PatchController draftPatchController = draftPatch.getDraftPatchController();
        draftPatchController.initDraftsTableView(previewerPatchController, new Passport(), SelectionMode.SINGLE);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, true, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(false, false, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.6));

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
    private void loadStackPaneInfo() {
        //ЗАГЛУШКА
        Label info = new Label("НЕТ ИНФОРМАЦИИ");
        info.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-font-style: oblique");
        infoStackPane = new StackPane(info);
        infoStackPane.setStyle("-fx-alignment: center");
        stpInfo.getChildren().add(infoStackPane);
    }

    /**
     * Создание предпросмотрщика
     */
    private void loadStackPanePreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical); //Предпросмотр
    }

    private BtnDouble createInfoOrDraftsTableButton(){
        BtnDouble btnInfoOrTable = new BtnDouble(
                BTN_INFO_IMG, "Показать информацию",
                BTN_TABLE_VIEW_IMG, "Показать чертежи",
                false);
        btnInfoOrTable.setOnAction(e->{
            if(btnInfoOrTable.getStateProperty().get()) {
                stpInfo.getChildren().clear();
                stpInfo.getChildren().add(0, draftPatch.getParent());
            } else {
                stpInfo.getChildren().clear();
                stpInfo.getChildren().add(0, infoStackPane);
            }
        });
        return btnInfoOrTable;
    }

}
