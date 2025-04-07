package ru.wert.tubus.chogori.previewer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.server.DelegatingServerHttpResponse;
import ru.wert.tubus.chogori.common.utils.TextUtils;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Remark;
import ru.wert.tubus.chogori.components.ZoomableScrollPane;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.entities.drafts.info.DraftInfoPatch;
import ru.wert.tubus.chogori.remarks.RemarksController;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.pdf.PDFReader;
import ru.wert.tubus.chogori.pdf.readers.PdfIcepdfReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSNewReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSOldReader;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.enums.EPDFViewer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static java.lang.String.format;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;
import static ru.wert.tubus.chogori.statics.AppStatic.*;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

//import ru.wert.datapik.client.entity.models.Draft;
@Slf4j
public class PreviewerPatchController {

    @Getter@FXML
    private HBox patchHeader;

    @FXML
    private Label bracket1;

    @FXML
    private Label bracket2;

    @FXML
    @Getter private StackPane paneViewer;

    @FXML
    @Getter private HBox hboxPreviewerButtons;

    @FXML
    private TextField tfDraftInfo;

    @Getter@FXML
    private Label lblCount;

    private Button btnShowRemarks;

    private ImageView imageView;

    public static List<Draft> HISTORY_PREVIEW = new ArrayList<>(); //Чертежи все, которые были в Превью
    public static List<String> SEARCH_HISTORY = new ArrayList<>(); //История для поиска, чертежи, по которым кликнули

    ContextMenu contextMenu;
    private PDFReader pdfReader; //см enum PDFViewer
    private StackPane pdfStackPane; //панель на которой работает pdfReader
    private boolean useBtnDownloadDXF;
    private boolean useBtnUpdateDraftView;
    private boolean useBtnOpenInNewTab;
    private boolean useBtnOpenInOuterApp;
    private boolean useBtnShowInfo;
    @Setter@Getter private Draft_TableView draftsTableView;
    private ObjectProperty<Draft> currentDraft = new SimpleObjectProperty<>();
    public Draft getCurrentDraft(){
        return this.currentDraft.get();
    };
    public void setCurrentDraft(Draft draft){
        currentDraft.setValue(draft);
    }

    private Button btnDownloadDXF;

    private File currentDraftPath;


    @FXML
    void initialize() {

        currentDraft.addListener((observable) -> {
            Draft draft = currentDraft.get();
            if(draft == null){
                tfDraftInfo.setText(" ");
                return;
            }
            EDraftStatus status = EDraftStatus.getStatusById(draft.getStatus());
            String text = "   " + draft.toUsefulString() + //Обозначение чертежа
                    " : " + EDraftType.getDraftTypeById(draft.getDraftType()).getShortName() + //Тип чертежа
                    "-" + draft.getPageNumber() + //страница
                    " : " + status.getStatusName();
            tfDraftInfo.setText(text); //Статус

            tfDraftInfo.setPrefWidth(TextUtils.computeTextWidth(tfDraftInfo.getFont(),
                    tfDraftInfo.getText(), 0.0D) + 20);

            if (status == EDraftStatus.LEGAL)
                tfDraftInfo.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: blue");
            else
                tfDraftInfo.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: darkred");

            if (useBtnDownloadDXF && DXF_DOCKS.contains(EDraftType.getDraftTypeById(draft.getDraftType()))) {
                if (!hboxPreviewerButtons.getChildren().contains(btnDownloadDXF))
                    hboxPreviewerButtons.getChildren().add(0, btnDownloadDXF);
            } else
                hboxPreviewerButtons.getChildren().remove(btnDownloadDXF);
        });

    }

    /**
     * Инициализирует класс стартовыми данными
     * @param viewer PDFViewer конкретный движок, с помощью которого открывается PDF
     * @param scene Scene
     */
    public void initPreviewer(EPDFViewer viewer, Scene scene){

        imageView = new ImageView();

        pdfStackPane = new StackPane();
        switch(viewer){
            case NEW_PDF_JS: pdfReader = new PdfJSNewReader(pdfStackPane); break; //Просмотр новым pdf.js
            case OLD_PDF_JS: pdfReader = new PdfJSOldReader(pdfStackPane); break; //Просмотр старым pdf.js
            default : pdfReader = new PdfIcepdfReader(pdfStackPane, scene); //Просмотр IcePDF
        }

        //Просмотрщик PDF по умолчанию
        paneViewer.getChildren().add(pdfStackPane);

    }

    public void initPreviewerToolBar(boolean useBtnDownloadDXF, boolean useBtnUpdateDraftView, boolean useBtnOpenInNewTab, boolean useBtnOpenInOuterApp,
                                     boolean useBtnShowInfo, boolean useBrackets){
        this.useBtnDownloadDXF = useBtnDownloadDXF;
        this.useBtnUpdateDraftView = useBtnUpdateDraftView;
        this.useBtnOpenInNewTab = useBtnOpenInNewTab;
        this.useBtnOpenInOuterApp = useBtnOpenInOuterApp;
        this.useBtnShowInfo = useBtnShowInfo;
        showBrackets(useBrackets);
        createPreviewerToolBar();
    }

    private void showBrackets(boolean useBrackets) {
        if(!useBrackets) {
            patchHeader.getChildren().remove(bracket1);
            patchHeader.getChildren().remove(bracket2);
        }
    }

    /**
     * Панель инструментов для ПРЕДПРОСМОТРА btn
     */
    private void createPreviewerToolBar() {
        Button btnWatchPreviewHistory = new Button();
        btnWatchPreviewHistory.setId("patchButton");
        btnWatchPreviewHistory.setGraphic(new ImageView(BtnImages.BTN_HISTORY_PREVIEW_IMG));
        btnWatchPreviewHistory.setTooltip(new Tooltip("История предпросмотров"));
        btnWatchPreviewHistory.setOnAction(event -> {
            if (HISTORY_PREVIEW.isEmpty()) return;
            List<Draft> list = new ArrayList<>(HISTORY_PREVIEW);
            Collections.reverse(list);
            ContextMenu menu = new ContextMenu();
            for(Draft d : list){
                String name = d.toUsefulString() + ", " + EDraftType.getDraftTypeById(d.getDraftType()).getShortName() + "-" + d.getPageNumber();
                MenuItem item = new MenuItem(name);
                item.setOnAction(e->openDraftInPreviewer(d, PreviewerPatchController.this, false));
                menu.getItems().add(item);
            }

            btnWatchPreviewHistory.setContextMenu(menu);
            menu.show((Node)event.getSource(), Side.LEFT, -38.0, 22.0);

        });

        //СКАЧАТЬ DXF файл
        btnDownloadDXF = new Button();
        btnDownloadDXF.setId("patchButton");
        btnDownloadDXF.setGraphic(new ImageView(BtnImages.BTN_DOWNLOAD_IMG));
        btnDownloadDXF.setTooltip(new Tooltip("Скачать"));
        btnDownloadDXF.setOnAction(event -> {
            if (currentDraft.get() == null) return;
            getDraftsTableView().getManipulator().downloadDrafts(Collections.singletonList(currentDraft.get()));
        });

//        currentDraft.get();
//        useBtnDownloadDXF = false;


        //ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        Button btnOpenInNewTab = new Button();
        btnOpenInNewTab.setId("patchButton");
        btnOpenInNewTab.setGraphic(new ImageView(BtnImages.BTN_OPEN_IN_NEW_TAB_IMG));
        btnOpenInNewTab.setTooltip(new Tooltip("Открыть в отдельной вкладке"));
        btnOpenInNewTab.setOnAction(event -> {
            if (currentDraft.get() == null) return;
            AppStatic.openDraftsInNewTabs(Collections.singletonList(currentDraft.get()));
        });

        //ОТКРЫТЬ В СТОРОННЕМ ПРИЛОЖЕНИИ
        Button openInOuterApp = new Button();
        openInOuterApp.setId("patchButton");
        openInOuterApp.setGraphic(new ImageView(BtnImages.BTN_OPEN_IN_OUTER_APP_IMG));
        openInOuterApp.setTooltip(new Tooltip("Открыть в отдельном приложении"));
        openInOuterApp.setOnAction(event -> {
                AppStatic.openInOuterApplication(currentDraft.get());
        });

        //ПОКАЗАТЬ ИНФОРМАЦИЮ
        Button btnShowInfo = new Button();
        btnShowInfo.setId("patchButton");
        btnShowInfo.setGraphic(new ImageView(BtnImages.BTN_INFO_IMG));
        btnShowInfo.setTooltip(new Tooltip("Показать информацию"));
        btnShowInfo.setOnAction(event -> {
            if(currentDraft.get() == null) return;
            new DraftInfoPatch().create(currentDraft.get(), event);
        });

        //ПОКАЗАТЬ КОММЕНТАРИИ
        btnShowRemarks = new Button();
        btnShowRemarks.setId("patchButton");
        btnShowRemarks.setGraphic(new ImageView(BtnImages.BTN_REMARKS_IMG));
        btnShowRemarks.setTooltip(new Tooltip("Показать комментарии"));
        btnShowRemarks.setOnAction(event -> {
            openRemarksEditor();
        });

        //ОБНОВИТЬ ИЗОБРАЖЕНИЕ
        Button updateDraftView = new Button();
        updateDraftView.setId("patchButton");
        updateDraftView.setGraphic(new ImageView(BtnImages.BTN_UPDATE_IMG));
        updateDraftView.setTooltip(new Tooltip("Обновить изображение"));
        updateDraftView.setOnAction(event -> {
            if(draftsTableView == null) return;
            Draft selectedDraft = draftsTableView.getSelectionModel().getSelectedItem();
            openDraftInPreviewer(selectedDraft, this, false);
        });

        hboxPreviewerButtons.getChildren().add(btnWatchPreviewHistory);
        if (useBtnUpdateDraftView) hboxPreviewerButtons.getChildren().add(updateDraftView);
        if (useBtnShowInfo) hboxPreviewerButtons.getChildren().add(btnShowInfo);
        if (useBtnOpenInOuterApp) hboxPreviewerButtons.getChildren().add(openInOuterApp);
        if (useBtnOpenInNewTab) hboxPreviewerButtons.getChildren().add(btnOpenInNewTab);

    }

    private void openRemarksEditor() {
        if(currentDraft.get() == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/remarks/remarks.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            Passport draftPassport = currentDraft.get().getPassport();
            RemarksController controller = loader.getController();
            controller.init(draftPassport);
            String tabName = "> " + draftPassport.toUsefulString();
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true,  null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает панель просмотрщика в зависимости от типа документа и добавляет ее
     * на главную панель paneViewer.
     * @param draftPath File
     */
    public void showDraft(File draftPath){
        this.currentDraftPath = draftPath;

        if(currentDraft.getValue() != null){
            boolean hasRemarks = CH_QUICK_DRAFTS.hasRemarks(currentDraft.getValue());
            if(hasRemarks){
                if(!hboxPreviewerButtons.getChildren().contains(btnShowRemarks)){
                    hboxPreviewerButtons.getChildren().add(0, btnShowRemarks);
                }
            } else {
                if(hboxPreviewerButtons.getChildren().contains(btnShowRemarks))
                    hboxPreviewerButtons.getChildren().removeAll(btnShowRemarks);
            }
        }

        //Вилка решений зависит от расширения файла
        String ext = FilenameUtils.getExtension(draftPath.getName()).toLowerCase();

        if (ext.equals("pdf")) {
            paneViewer.getChildren().set(0, pdfStackPane);
            try {
                pdfReader.showPDF(draftPath);
            } catch (Exception e) {
                log.error("showDraft : something went wrong with showing pdf!");
                e.printStackTrace();
            }
        } else if (SOLID_EXTENSIONS.contains(ext)) {
            showPlaceholder(BtnImages.SOLID_3D_IMG);
        } else if (DXF_EXTENSIONS.contains(ext)) {
            showPlaceholder(BtnImages.DXF_IMG);
        } else {
            try {
                showImage(draftPath);
            }catch(Exception ex){
                log.error("showDraft : something went wrong with showing image!");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Метод отображает соответствующее информационное изображение
     */
    private void showPlaceholder(Image image) {
        imageView.setImage(image);
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        StackPane pane = new StackPane(imageView);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: white");
        paneViewer.getChildren().set(0, pane);
        paneViewer.autosize();
    }

    /**
     * Метод открывает файл в превьюере
     * @param draftPath File
     */
    private void showImage(File draftPath) {

        try {
            Image imageDraft = new Image(draftPath.toURI().toURL().toString());
            imageView.setImage(imageDraft);
            imageView.setFitWidth(imageDraft.getWidth());
            imageView.setFitHeight(imageDraft.getHeight());

            ScrollPane scrollPane = new ZoomableScrollPane(imageView, paneViewer);
            paneViewer.getChildren().set(0, scrollPane);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод дополнительно передает объект Draft? который используется для открытия файла в отдельной вкладке
     * Передаем null, чтобы купировать попытку открыть NO_IMAGE.pdf файла
     * @param currentDraft Draft
     * @param currentDraftPath File
     */
    public void showDraft(Draft currentDraft, File currentDraftPath){
        this.currentDraft.set(currentDraft);
        this.currentDraftPath = currentDraftPath;

        addPreviewToHistory(currentDraft);


        showDraft(currentDraftPath);
    }

    /**
     * Метод вызывается для добавления просмотренного чертежа к истории просмотров
     * Последний просмотренный чертеж всегда добавляется в конец списка
     */
    public static void addPreviewToHistory(Draft draft){
        if(draft == null) return;
        HISTORY_PREVIEW.removeIf(d -> d.equals(draft));
        HISTORY_PREVIEW.add(draft);
    }



}
