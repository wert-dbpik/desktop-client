package ru.wert.datapik.utils.entities.drafts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.utils.common.components.BtnGlobe;
import ru.wert.datapik.utils.common.components.BtnMenuDraftsColumns;
import ru.wert.datapik.utils.common.components.BtnMenuDraftsFilter;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Draft_PatchController {

    @FXML
    @Getter
    private HBox hboxDraftsButtons;

    @FXML
    private VBox vboxDrafts;


    @Getter private Draft_TableView draftsTable;
    private PreviewerPatchController previewerController;
    private Object modifyingClass; //класс, от которого зависит отображаемый список в таблице (Folder, Product, Passport)
    private SelectionMode mode; //SelectionMode.SINGLE, SelectionMode.MULTIPLE
    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;

    //Кнопки toolbar
    private boolean useBtnDraftsGlobe; //Показать все чертежи
    private boolean useBtnShowFilter; //Фильтровать список
    private boolean useBtnShowColumns;
    private boolean useContextMenu;

    public void initDraftsTableView(PreviewerPatchController previewerController, Object modifyingClass, SelectionMode mode, boolean useContextMenu){
        this.previewerController = previewerController;
        this.modifyingClass = modifyingClass;
        this.mode = mode;
        this.useContextMenu = useContextMenu;

        createDraftTableView();

    }

    public void initDraftsToolBar(boolean btnDraftsGlobe, boolean btnShowFilter, boolean btnShowColumns){
        this.useBtnDraftsGlobe = btnDraftsGlobe;
        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;

        createDraftToolBar();
    }

    private void createDraftTableView() {
        //запуск новой версии
        draftsTable = new Draft_TableView("ЧЕРТЕЖ", previewerController, useContextMenu);
        draftsTable.setModifyingClass(modifyingClass);
        draftsTable.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(draftsTable, Priority.ALWAYS);
        vboxDrafts.getChildren().add(draftsTable);

        CH_SEARCH_FIELD.setSearchableTableController(draftsTable);
        draftsTable.updateView();

    }

    private void createDraftToolBar() {

        //Кнопка ПОКАЗАТЬ ВСЕ
        Button btnDraftsGlobe = new BtnGlobe<>(draftsTable);

        //Кнопка ПОКАЗАТЬ ФИЛЬТР
        btnShowFilter = new BtnMenuDraftsFilter(draftsTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuDraftsColumns(draftsTable);

        if(useBtnShowFilter) hboxDraftsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxDraftsButtons.getChildren().add(btnShowColumns);
        if(useBtnDraftsGlobe) hboxDraftsButtons.getChildren().add(btnDraftsGlobe);

    }

}
