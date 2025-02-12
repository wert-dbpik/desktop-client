package ru.wert.tubus.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.tubus.chogori.components.BtnDouble;
import ru.wert.tubus.chogori.components.BtnMenuDraftsColumns;
import ru.wert.tubus.chogori.components.BtnMenuDraftsFilter;
import ru.wert.tubus.chogori.components.BtnMenuDraftsFilterDocs;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.ProductGroup;

import java.util.Collections;

import static ru.wert.tubus.chogori.images.BtnImages.*;

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
    @Getter private MenuButton btnShowFilterDocs;
    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;

    @Getter private BtnDouble btnShowDraftsDocks;
    @Getter private BtnDouble btnShowDFXDocks;

    //Кнопки toolbar
    private boolean useBtnDraftsGlobe; //Показать все чертежи
    private boolean useBtnShowFilter; //Фильтровать список
    private boolean useBtnShowColumns; //Выбор колонок для отображения
    private boolean useBtnShowDFX; //Выбор документов для отображения
    private boolean switchDraftsSearch; //Переключение на поиск чертежей при переносе фокуса на таблицу с чертежами


    public void initDraftsTableView(PreviewerPatchController previewerController, Object modifyingClass, SelectionMode mode, boolean switchDraftsSearch){
        this.previewerController = previewerController;
        this.modifyingClass = modifyingClass;
        this.mode = mode;
        this.switchDraftsSearch = switchDraftsSearch;

        lblSourceOfDrafts.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: darkblue");

        createDraftTableView();

    }

    public void initDraftsToolBar(boolean btnDraftsGlobe, boolean btnShowFilter, boolean btnShowColumns, boolean btnShowDFX){
        this.useBtnDraftsGlobe = btnDraftsGlobe;
        this.useBtnShowFilter = btnShowFilter;
        this.useBtnShowColumns = btnShowColumns;
        this.useBtnShowDFX = btnShowDFX;

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
        draftsTable.setDraftPatchController(this); //Нужен для доступа к полю
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
            draftsTable.setModifyingItem(null);
            draftsTable.updateView();
            draftsTable.requestFocus();
            showSourceOfPassports(null);
            e.consume();
        });

        btnShowDraftsDocks = new BtnDouble(
                BTN_SHOW_DRAFTS_DOCKS_OFF_IMG, "Черетежи OFF",
                BTN_SHOW_DRAFTS_DOCKS_ON_IMG, "Черетежи ON",
                true
        );
        btnShowDraftsDocks.getStateProperty().addListener((observable, oldValue, newValue) -> {
            draftsTable.setShowDraftDocks(newValue);
            Platform.runLater(() -> {
                Draft selectedDraft = draftsTable.getSelectionModel().getSelectedItem();
                draftsTable.updateRoutineTableView(Collections.singletonList(selectedDraft), false);
                draftsTable.refresh();
            });
        });

        btnShowDFXDocks = new BtnDouble(
                BTN_SHOW_DFX_DOCKS_OFF_IMG, "Развертки OFF",
                BTN_SHOW_DFX_DOCKS_ON_IMG, "Развертки ON",
                false
        );
        btnShowDFXDocks.getStateProperty().addListener((observable, oldValue, newValue) -> {
            draftsTable.setShowDFXDocks(newValue);
            Platform.runLater(() -> {
                Draft selectedDraft = draftsTable.getSelectionModel().getSelectedItem();
                draftsTable.updateRoutineTableView(Collections.singletonList(selectedDraft), false);
                draftsTable.refresh();
            });
        });

        //Кнопка ПОКАЗАТЬ ФИЛЬТР ДОКУМЕНТОВ
//        btnShowFilterDocs = new BtnMenuDraftsFilterDocs(draftsTable);

        //Кнопка ПОКАЗАТЬ ФИЛЬТР
        btnShowFilter = new BtnMenuDraftsFilter(draftsTable);

        //Кнопка ПОКАЗАТЬ КОЛОНКИ
        btnShowColumns = new BtnMenuDraftsColumns(draftsTable);

        if(useBtnShowDFX){
            hboxDraftsButtons.getChildren().add(btnShowDraftsDocks);
            hboxDraftsButtons.getChildren().add(btnShowDFXDocks);
        }
        if(useBtnShowFilter) hboxDraftsButtons.getChildren().add(btnShowFilter);
        if(useBtnShowColumns) hboxDraftsButtons.getChildren().add(btnShowColumns);
        if(useBtnDraftsGlobe) hboxDraftsButtons.getChildren().add(btnDraftsGlobe);

    }

}
