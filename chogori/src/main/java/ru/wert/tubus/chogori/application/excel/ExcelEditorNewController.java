package ru.wert.tubus.chogori.application.excel;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.chogori.application.common.CommonUnits;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.chogori.common.components.BtnDouble;
import ru.wert.tubus.chogori.excel.model.EditorRow;
import ru.wert.tubus.chogori.excel.table.Excel_Patch;
import ru.wert.tubus.chogori.excel.table.Excel_PatchController;
import ru.wert.tubus.chogori.excel.table.Excel_TableView;
import ru.wert.tubus.chogori.entities.drafts.Draft_Patch;
import ru.wert.tubus.chogori.entities.drafts.Draft_PatchController;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.info.InfoPatch;
import ru.wert.tubus.chogori.info.InfoPatchController;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.images.BtnImages;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;

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

        loadStackPanePreviewer();
        loadStackPaneDrafts();
        loadStackPaneExcel();
        loadStackPaneInfo();

    }

    /**
     * Создание предпросмотрщика
     */
    private void loadStackPanePreviewer() {
        previewerPatchController =
                CommonUnits.loadStpPreviewer(stpPreviewer, sppHorizontal, sppVertical, true); //Предпросмотр
    }

    /**
     * Создание левой панели с excel таблицей
     */
    private void loadStackPaneExcel() {

        Excel_Patch excelPatch = new Excel_Patch().create();

        excelPatchController = excelPatch.getExcelPatchController();
        excelPatchController.initExcelTableView(excelFile, previewerPatchController, false);
        excelPatchController.initExcelToolBar(true, true);
        //Добавляем кнопки на панель
        excelPatchController.getHbButtons().getChildren().add(createInfoOrDraftsTableButton());
        excelPatchController.getHbButtons().getChildren().add(CommonUnits.createHorizontalDividerButton(sppHorizontal, 0.8, 0.6));
        //Наименование файла
        excelPatchController.getLblExcelFile().setText(excelFile.getName());
        excelTable = excelPatchController.getExcelTable();

        setNotEditableTableSettings();

        stpExcel.getChildren().add(excelPatch.getParent());

    }

    private void setNotEditableTableSettings(){
        excelTable.setId("excelViewer");
        excelTable.setEditable(false);
        excelTable.getSelectionModel().setCellSelectionEnabled(false);
        excelTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Prefix prefix = CH_QUICK_PREFIXES.findByName("ПИК");
        excelTable.getRowNumber().setVisible(false);
        excelTable.getKrp().setVisible(false);
        excelTable.getMaterial().setVisible(false);
        excelTable.getParamA().setVisible(false);
        excelTable.getParamB().setVisible(false);

        ((TableView<EditorRow>)excelTable).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue != null && newValue != oldValue) {
                String number = newValue.getDecNumber();

                Pattern p = Pattern.compile("\\d{3}.?\\d{3}\\.\\d{3}"); //Децимальный номер xxxxxx.xxx
                Matcher m = p.matcher(number);
                String decNumber = "";
                while (m.find()) {
                    decNumber = number.substring(m.start(), m.end());
                }

                Passport passport = CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(prefix, decNumber);

                Platform.runLater(() -> {
                    draftsTable.setModifyingItem(passport);
                    draftsTable.updateView();
                });
            }
        });

        excelTable.setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.RIGHT)) draftsTable.getManipulator().goDraftsForward();
            else if(e.getCode().equals(KeyCode.LEFT)) draftsTable.getManipulator().goDraftsBackward();
        });
    }

    /**
     * Создание панели с таблицей доступных чертежей
     */
    private void loadStackPaneDrafts() {

        draftPatch = new Draft_Patch().create();

        Draft_PatchController draftPatchController = draftPatch.getDraftPatchController();
        draftPatchController.initDraftsTableView(previewerPatchController, new Passport(), SelectionMode.MULTIPLE, false);
        draftsTable = draftPatchController.getDraftsTable();
        draftsTable.showTableColumns(false, true, true, true, false,
                false, true);
        //Инструментальную панель инициируем в последнюю очередь
        draftPatchController.initDraftsToolBar(false, false, true, true);
        draftPatchController.getHboxDraftsButtons().getChildren().add(CommonUnits.createVerticalDividerButton(sppVertical, 0.8, 0.6));

        draftPatch.connectWithPreviewer(draftsTable, previewerPatchController);

        previewerPatchController.getLblCount().textProperty().bind(
                Bindings.convert(draftsTable.getPreparedList().sizeProperty()));

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
//        stpInfo.getChildren().add(infoStackPane);
    }

//    private void createStackPaneInfo



    private BtnDouble createInfoOrDraftsTableButton(){
        BtnDouble btnInfoOrTable = new BtnDouble(
                BtnImages.BTN_TABLE_VIEW_IMG, "Показать чертежи",
                BtnImages.BTN_INFO_IMG, "Показать информацию",
                true);
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
