package ru.wert.datapik.utils.previewer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.views.pdf.PDFReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfIcepdfReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSNewReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSOldReader;
import ru.wert.datapik.winform.enums.EPDFViewer;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;

import static ru.wert.datapik.utils.images.BtnImages.BTN_OPEN_IN_NEW_TAB_IMG;

//import ru.wert.datapik.client.entity.models.Draft;

public class PreviewerPatchController {

    @FXML
    private StackPane paneViewer;

    @FXML
    @Getter private HBox hboxPreviewerButtons;

    private PDFReader pdfReader; //см enum PDFViewer
    private StackPane pdfStackPane; //панель на которой работает pdfReader
    private boolean useBtnOpenInNewTab;
    @Setter private Draft_TableView draftsTableView;
    private Draft currentDraft;


    @FXML
    void initialize() {
    }

    /**
     * Инициализирует класс стартовыми данными
     * @param viewer PDFViewer конкретный движок, с помощью которого открывается PDF
     * @param scene Scene
     */
    public void initPreviewer(EPDFViewer viewer, Scene scene){
//        this.draftsTableView = draftsTableView;
        pdfStackPane = new StackPane();
        switch(viewer){
            case NEW_PDF_JS: pdfReader = new PdfJSNewReader(pdfStackPane); break; //Просмотр новым pdf.js
            case OLD_PDF_JS: pdfReader = new PdfJSOldReader(pdfStackPane); break; //Просмотр старым pdf.js
            default : pdfReader = new PdfIcepdfReader(pdfStackPane, scene); //Просмотр IcePDF
        }

        //Просмотрщик PDF по умолчанию
        paneViewer.getChildren().add(pdfStackPane);

    }

    public void initPreviewerToolBar(boolean useBtnOpenInNewTab){
        this.useBtnOpenInNewTab = useBtnOpenInNewTab;

        createPreviewerToolBar();
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
            if(currentDraft == null) return;
            AppStatic.openDraftsInNewTabs(Collections.singletonList(currentDraft));
        });

        if (useBtnOpenInNewTab) hboxPreviewerButtons.getChildren().add(btnOpenInNewTab);

    }

    /**
     * Создает панель просмотрщика в зависимости от типа документа и добавляет ее
     * на главную панель paneViewer.
     * @param draftPath File
     */
    public void showDraft(File draftPath){
        //Вилка решений зависит от расширения файла
        String ext = FilenameUtils.getExtension(draftPath.getName()).toLowerCase();

        if (ext.equals("pdf")) {
            paneViewer.getChildren().set(0,pdfStackPane);
            pdfReader.showPDF(draftPath);
        } else {
            showImage(draftPath);
        }
    }

    /**
     * Метод открывает файл в превьюере
     * @param draftPath File
     */
    private void showImage(File draftPath) {
        try {
            Image imageDraft = new Image(draftPath.toURI().toURL().toString());
            ImageView imageView = new ImageView(imageDraft);
            imageView.setFitWidth(imageDraft.getWidth());
            imageView.setFitHeight(imageDraft.getHeight());
            //
            ScrollPane scrollPane = new ScrollPane(imageView);

            Platform.runLater(() -> {
                paneViewer.getChildren().set(0, scrollPane);
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод дополнительно передает объект Draft? который используется для открытия файла в отдельной вкладке
     * Передаем null, чтобы купировать попытку открыть NO_IMAGE.pdf файла
     * @param currentDraft Draft
     * @param draftPath File
     */
    public void showDraft(Draft currentDraft, File draftPath){
        this.currentDraft = currentDraft;

        showDraft(draftPath);
    }

}
