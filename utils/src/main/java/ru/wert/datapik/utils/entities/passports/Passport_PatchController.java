package ru.wert.datapik.utils.entities.passports;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.utils.common.components.BtnGlobe;
import ru.wert.datapik.utils.common.components.BtnMenuPassportsColumns;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Passport_PatchController {

    @FXML
    @Getter
    private HBox hboxPassportsButtons;

    @FXML
    private VBox vboxPassports;

    @Getter private Passport_TableView passportsTable;
    private PreviewerPatchController previewerController;
    private Object modifyingClass; //класс, от которого зависит отображаемый список в таблице (Folder, Product, Passport)
    private SelectionMode mode; //SelectionMode.SINGLE, SelectionMode.MULTIPLE
//    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;
    private Draft_TableView draftsTable;

    //Кнопки toolbar
    private boolean useBtnPassportsGlobe; //Показать все индентификаторы
//    private boolean useBtnShowFilter; //Фильтровать список
    private boolean useBtnShowColumns;
    private boolean useContextMenu;

    public void initPassportsTableView(PreviewerPatchController previewerController, Object modifyingClass, SelectionMode mode, boolean useContextMenu){
        this.previewerController = previewerController;
        this.modifyingClass = modifyingClass;
        this.mode = mode;
        this.useContextMenu = useContextMenu;

        createPassportTableView();

    }

    public void initPassportsToolBar(boolean btnPassportsGlobe, boolean btnShowColumns){
        this.useBtnPassportsGlobe = btnPassportsGlobe;
//        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;

        createPassportToolBar();
    }

    private void createPassportTableView() {
        //запуск новой версии
        passportsTable = new Passport_TableView("ИДЕНТИФИКАТОР", previewerController, useContextMenu);
        passportsTable.setModifyingClass(modifyingClass);
        passportsTable.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(passportsTable, Priority.ALWAYS);
        vboxPassports.getChildren().add(passportsTable);

//        CH_SEARCH_FIELD.setSearchableTableController(passportsTable);
        passportsTable.updateView();

    }

    private void createPassportToolBar() {

        //Кнопка ПОКАЗАТЬ ВСЕ
        Button btnPassportsGlobe = new BtnGlobe<>(passportsTable);

        //Кнопка ПОКАЗАТЬ ФИЛЬТР
//        btnShowFilter = new BtnMenuDraftsFilter(passportsTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuPassportsColumns(passportsTable);

//        if(useBtnShowFilter) hboxPassportsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxPassportsButtons.getChildren().add(btnShowColumns);
        if(useBtnPassportsGlobe) hboxPassportsButtons.getChildren().add(btnPassportsGlobe);

    }



}
