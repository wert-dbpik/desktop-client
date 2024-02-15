package ru.wert.tubus.chogori.entities.drafts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.common.components.BtnMenuDraftsColumns;
import ru.wert.tubus.chogori.common.components.BtnMenuDraftsFilter;
import ru.wert.tubus.chogori.images.BtnImages;

public class Draft_PatchController {

    @FXML
    @Getter
    private HBox hboxDraftsButtons;

    @FXML
    private VBox vboxDrafts;

    @FXML
    private Label lblShownFolder;

    @Getter @FXML
    private Label lblSourceOfDrafts;

    @Getter @FXML
    private Label lblNumberOfDrafts;


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
    private boolean switchDraftsSearch; //Переключение на поиск чертежей при переносе фокуса на таблицу с чертежами


    public void initDraftsTableView(PreviewerPatchController previewerController, Object modifyingClass, SelectionMode mode, boolean switchDraftsSearch){
        this.previewerController = previewerController;
        this.modifyingClass = modifyingClass;
        this.mode = mode;
        this.switchDraftsSearch = switchDraftsSearch;

        lblSourceOfDrafts.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: darkblue");

        createDraftTableView();

    }

    public void initDraftsToolBar(boolean btnAltSwitcher, boolean btnDraftsGlobe, boolean btnShowFilter, boolean btnShowColumns){
        this.useBtnDraftsGlobe = btnDraftsGlobe;
        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;
        this.useBtnAltSwitcher = btnAltSwitcher;

        createDraftToolBar();
    }

    /**
     * Выводится папка в каталоге или комплект, куда входят найденные записи
     * @param source Object
     */
    public void showSourceOfPassports(Object source){
        if(source != null){
            if(source instanceof Folder)
                lblSourceOfDrafts.setText(((Folder) source).getName());
            else if(source instanceof ProductGroup)
                lblSourceOfDrafts.setText(((ProductGroup) source).getName());
        } else {
            lblSourceOfDrafts.setText("");
        }
    }

    private void createDraftTableView() {
        //запуск новой версии
        draftsTable = new Draft_TableView("ЧЕРТЕЖ", previewerController, vboxDrafts, switchDraftsSearch);
        draftsTable.setModifyingClass(modifyingClass);
        draftsTable.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(draftsTable, Priority.ALWAYS);
        vboxDrafts.getChildren().add(draftsTable);

        draftsTable.updateView();

    }

    private void createDraftToolBar() {
        //Надпись о текущем количестве чертежей в таблице
        lblNumberOfDrafts.setStyle("-fx-font-weight: bold; -fx-font-style: normal; -fx-text-fill: black");
        lblNumberOfDrafts.textProperty().bind(draftsTable.getPreparedList().sizeProperty().asString(" (%d шт)"));

        //Кнопка ПОКАЗАТЬ ВСЕ
        Button btnDraftsGlobe = new Button();
        btnDraftsGlobe.setGraphic(new ImageView(BtnImages.BTN_GLOBE_IMG));
        btnDraftsGlobe.setTooltip(new Tooltip("Показать все"));

        btnDraftsGlobe.setId("patchButton");
        btnDraftsGlobe.setOnAction(e->{
            draftsTable.setSelectedFolders(null);
            draftsTable.setSearchedText("");
            draftsTable.setModifyingItem(null);
            draftsTable.updateView();
            showSourceOfPassports(null);
            e.consume();
        });


        //Кнопка ПОКАЗАТЬ ФИЛЬТР
        btnShowFilter = new BtnMenuDraftsFilter(draftsTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuDraftsColumns(draftsTable);

        if(useBtnShowFilter) hboxDraftsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxDraftsButtons.getChildren().add(btnShowColumns);
        if(useBtnDraftsGlobe) hboxDraftsButtons.getChildren().add(btnDraftsGlobe);

    }

}
