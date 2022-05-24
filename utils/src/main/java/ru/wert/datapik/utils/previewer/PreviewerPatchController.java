package ru.wert.datapik.utils.previewer;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.components.ZoomableScrollPane;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.drafts.commands._Draft_Commands;
import ru.wert.datapik.utils.entities.drafts.info.DraftInfoPatch;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.views.pdf.PDFReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfIcepdfReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSNewReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSOldReader;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.statics.WinformStatic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.statics.AppStatic.SOLID_VIEWER_EXTENSIONS;
import static ru.wert.datapik.utils.statics.AppStatic.openDraftInPreviewer;

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
    private StackPane paneViewer;

    @FXML
    @Getter private HBox hboxPreviewerButtons;

    @FXML
    private Label lblDraftInfo;

    @Getter@FXML
    private Label lblCount;

    ImageView imageView;

    ContextMenu contextMenu;
    private PDFReader pdfReader; //см enum PDFViewer
    private StackPane pdfStackPane; //панель на которой работает pdfReader
    private boolean useBtnUpdateDraftView;
    private boolean useBtnOpenInNewTab;
    private boolean useBtnOpenInOuterApp;
    private boolean useBtnShowInfo;
    @Setter@Getter private Draft_TableView draftsTableView;
    private ObjectProperty<Draft> currentDraft = new SimpleObjectProperty<>();
    public Draft getCurrentDraft(){return this.currentDraft.get();};

    private File currentDraftPath;


    @FXML
    void initialize() {

        currentDraft.addListener((observable) -> {
            Draft draft = currentDraft.get();
            if(draft == null){
                lblDraftInfo.setText("   ...");
                lblDraftInfo.setStyle("-fx-font-weight: bold;  -fx-text-fill: black");
                return;
            }
            EDraftStatus status = EDraftStatus.getStatusById(draft.getStatus());
            lblDraftInfo.setText(
                    "   " + draft.toUsefulString() + //Обозначение чертежа
                            " : " + EDraftType.getDraftTypeById(draft.getDraftType()).getShortName() + //Тип чертежа
                            "-" + draft.getPageNumber() + //страница
                            " : " + status.getStatusName()); //Статус
            if (status == EDraftStatus.LEGAL)
                lblDraftInfo.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: blue");
            else
                lblDraftInfo.setStyle("-fx-font-weight: normal; -fx-font-style: oblique; -fx-text-fill: darkred");
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

    public void initPreviewerToolBar(boolean useBtnUpdateDraftView, boolean useBtnOpenInNewTab, boolean useBtnOpenInOuterApp, boolean useBtnShowInfo, boolean useBrackets){
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
     * Панель инструментов для ПРЕДПРОСМОТРА
     */
    private void createPreviewerToolBar() {
        //ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        Button btnOpenInNewTab = new Button();
        btnOpenInNewTab.setId("patchButton");
        btnOpenInNewTab.setGraphic(new ImageView(BTN_OPEN_IN_NEW_TAB_IMG));
        btnOpenInNewTab.setTooltip(new Tooltip("Открыть в отдельной вкладке"));
        btnOpenInNewTab.setOnAction(event -> {
            if(currentDraft.get() == null) return;
            AppStatic.openDraftsInNewTabs(Collections.singletonList(currentDraft.get()), draftsTableView);
        });

        //ОТКРЫТЬ В СТОРОННЕМ ПРИЛОЖЕНИИ
        Button openInOuterApp = new Button();
        openInOuterApp.setId("patchButton");
        openInOuterApp.setGraphic(new ImageView(BTN_OPEN_IN_OUTER_APP_IMG));
        openInOuterApp.setTooltip(new Tooltip("Открыть в отдельном приложении"));
        openInOuterApp.setOnAction(event -> {
            File myFile;
            //Если отображается чертеж из БД
            if (currentDraft.get() == null)
                myFile = currentDraftPath;
            else
                //Если отображается вновь добавляемый файл
                myFile = new File(WinformStatic.WF_TEMPDIR + File.separator +
                    currentDraft.get().getId() + "." + currentDraft.get().getExtension());

            if (myFile.exists() && myFile.isFile())
                try {
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    log.error("createPreviewerToolBar : couldn'n load file '{}'", myFile);
                }
        });

        Button btnShowInfo = new Button();
        btnShowInfo.setId("patchButton");
        btnShowInfo.setGraphic(new ImageView(BTN_INFO_IMG));
        btnShowInfo.setTooltip(new Tooltip("Показать информацию"));
        btnShowInfo.setOnAction(event -> {
            if(currentDraft.get() == null) return;
            new DraftInfoPatch().create(currentDraft.get(), event);
        });

        Button updateDraftView = new Button();
        updateDraftView.setId("patchButton");
        updateDraftView.setGraphic(new ImageView(BTN_UPDATE_IMG));
        updateDraftView.setTooltip(new Tooltip("Обновить изображение"));
        updateDraftView.setOnAction(event -> {
            Draft selectedDraft = draftsTableView.getSelectionModel().getSelectedItem();
            openDraftInPreviewer(selectedDraft, this);
        });

        if (useBtnUpdateDraftView) hboxPreviewerButtons.getChildren().add(updateDraftView);
        if (useBtnShowInfo) hboxPreviewerButtons.getChildren().add(btnShowInfo);
        if (useBtnOpenInOuterApp) hboxPreviewerButtons.getChildren().add(openInOuterApp);
        if (useBtnOpenInNewTab) hboxPreviewerButtons.getChildren().add(btnOpenInNewTab);

    }

    /**
     * Создает панель просмотрщика в зависимости от типа документа и добавляет ее
     * на главную панель paneViewer.
     * @param draftPath File
     */
    public void showDraft(File draftPath){
        this.currentDraftPath = draftPath;
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
        } else if (SOLID_VIEWER_EXTENSIONS.contains(ext)) {
            showPlaceholder(SOLID_3D_IMG);
        } else {
            paneViewer.getChildren().clear();
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
        paneViewer.getChildren().clear();
        imageView.setImage(image);
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        StackPane pane = new StackPane(imageView);
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: white");
        paneViewer.getChildren().add(0, pane);
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


        showDraft(currentDraftPath);
    }

}
