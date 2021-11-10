package ru.wert.datapik.utils.editor.table;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.utils.common.components.BtnGlobe;
import ru.wert.datapik.utils.editor.poi.POIReader;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Excel_PatchController {

    @FXML
    @Getter
    private Label lblExcelFile;

    @FXML
    @Getter
    private HBox hbButtons;

    @FXML
    private StackPane stpExcelTable;


    @Getter private TableView excelTable;
    private PreviewerPatchController previewerController;
    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;
    private File excelFile;
    private POIReader poiReader;

    //Кнопки toolbar
    private boolean useBtnShowFilter; //Фильтровать список
    private boolean useBtnShowColumns;
    private boolean useContextMenu;

    public void initExcelTableView(File excelFile, PreviewerPatchController previewerController, boolean useContextMenu){
        this.excelFile = excelFile;
        this.previewerController = previewerController;
        this.useContextMenu = useContextMenu;

        createExcelTableView();

    }

    public void initExcelToolBar(boolean btnShowFilter, boolean btnShowColumns){

        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;

        createExcelToolBar();
    }

    private void createExcelTableView() {
        //запуск новой версии
//        excelTable = new Excel_TableView("ЧЕРТЕЖ", previewerController, useContextMenu);
        try {
            poiReader = new POIReader(excelFile);
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.TOP_CENTER);
            excelTable = new Excel_TableView(poiReader, hbox).getTableView();
            excelTable.setEditable(true);
            hbox.getChildren().add(excelTable);
            excelTable.refresh();
            stpExcelTable.getChildren().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        CH_SEARCH_FIELD.setSearchableTableController(excelTable);
//        excelTable.updateView();

    }

    private void createExcelToolBar() {

        //Кнопка ПОКАЗАТЬ ФИЛЬТР
//        btnShowFilter = new BtnMenuExcelFilter(excelTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
//        btnShowColumns = new BtnMenuExcelsColumns(excelTable);

//        if(useBtnShowFilter) hbButtons.getChildren().add(btnShowFilter);
//        if(useBtnShowColumns) hbButtons.getChildren().add(btnShowColumns);

    }

}
