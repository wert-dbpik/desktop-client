package ru.wert.tubus.chogori.entities.drafts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.common.components.BtnDoubleAlt;
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

        lblSourceOfDrafts.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: blue");

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
        draftsTable = new Draft_TableView("ЧЕРТЕЖ", previewerController, vboxDrafts);
        draftsTable.setModifyingClass(modifyingClass);
        draftsTable.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(draftsTable, Priority.ALWAYS);
        vboxDrafts.getChildren().add(draftsTable);

        draftsTable.updateView();

    }

    private void createDraftToolBar() {

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

        BtnDoubleAlt<Draft> btnDoubleAlt =new BtnDoubleAlt<>(draftsTable, false);
        Button btnAltOn = btnDoubleAlt.create();
        draftsTable.getAltOnProperty().bindBidirectional(btnDoubleAlt.getStateProperty());
        btnDoubleAlt.getStateProperty().set(false);


        //Кнопка ПОКАЗАТЬ ФИЛЬТР
        btnShowFilter = new BtnMenuDraftsFilter(draftsTable);
        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuDraftsColumns(draftsTable);

        if(useBtnAltSwitcher) hboxDraftsButtons.getChildren().add(btnAltOn);
        if(useBtnShowFilter) hboxDraftsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxDraftsButtons.getChildren().add(btnShowColumns);
        if(useBtnDraftsGlobe) hboxDraftsButtons.getChildren().add(btnDraftsGlobe);

    }

}
