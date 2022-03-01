package ru.wert.datapik.utils.entities.drafts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.components.*;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import static ru.wert.datapik.utils.images.BtnImages.BTN_GLOBE_IMG;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Draft_PatchController {

    @FXML
    @Getter
    private HBox hboxDraftsButtons;

    @FXML
    private VBox vboxDrafts;

    @FXML
    private Label lblShownFolder;


    @Getter private Draft_TableView draftsTable;
    private PreviewerPatchController previewerController;
    private Object modifyingClass; //класс, от которого зависит отображаемый список в таблице (Folder, Product, Passport)
    private SelectionMode mode; //SelectionMode.SINGLE, SelectionMode.MULTIPLE
    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;

    //Кнопки toolbar
    private boolean useBtnDraftsGlobe; //Показать все чертежи
    private boolean useBtnShowFilter; //Фильтровать список
    private boolean useBtnShowColumns; //Выбор колонок для отображения
    private boolean useBtnAltSwitcher;//Переключатель Alt для перехода к предпросмотру


    public void initDraftsTableView(PreviewerPatchController previewerController, Object modifyingClass, SelectionMode mode){
        this.previewerController = previewerController;
        this.modifyingClass = modifyingClass;
        this.mode = mode;

        createDraftTableView();

    }

    public void initDraftsToolBar(boolean btnAltSwitcher, boolean btnDraftsGlobe, boolean btnShowFilter, boolean btnShowColumns){
        this.useBtnDraftsGlobe = btnDraftsGlobe;
        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;
        this.useBtnAltSwitcher = btnAltSwitcher;

        createDraftToolBar();
    }

    private void createDraftTableView() {
        //запуск новой версии
        draftsTable = new Draft_TableView("ЧЕРТЕЖ", previewerController);
        draftsTable.setModifyingClass(modifyingClass);
        draftsTable.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(draftsTable, Priority.ALWAYS);
        vboxDrafts.getChildren().add(draftsTable);

        CH_SEARCH_FIELD.setSearchableTableController(draftsTable);
        draftsTable.updateView();

    }

    private void createDraftToolBar() {

        //Кнопка ПОКАЗАТЬ ВСЕ
        Button btnDraftsGlobe = new Button();
        btnDraftsGlobe.setGraphic(new ImageView(BTN_GLOBE_IMG));
        btnDraftsGlobe.setTooltip(new Tooltip("Показать все"));

        btnDraftsGlobe.setId("patchButton");
        btnDraftsGlobe.setOnAction(e->{
            draftsTable.setSelectedFoldersForContextMenu(null);
            draftsTable.setSearchedText("");
            draftsTable.setModifyingItem(null);
            draftsTable.updateView();
            e.consume();
        });

        Button btnUseAltToPreview = new BtnDoubleAlt<>(draftsTable).create();
//        btnUseAltToPreview.setL


        //Кнопка ПОКАЗАТЬ ФИЛЬТР
        btnShowFilter = new BtnMenuDraftsFilter(draftsTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuDraftsColumns(draftsTable);

        if(useBtnAltSwitcher) hboxDraftsButtons.getChildren().add(btnUseAltToPreview);
        if(useBtnShowFilter) hboxDraftsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxDraftsButtons.getChildren().add(btnShowColumns);
        if(useBtnDraftsGlobe) hboxDraftsButtons.getChildren().add(btnDraftsGlobe);

    }

}
