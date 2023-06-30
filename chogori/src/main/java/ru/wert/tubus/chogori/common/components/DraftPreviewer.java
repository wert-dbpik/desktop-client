package ru.wert.tubus.chogori.common.components;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.commons.io.FilenameUtils;
import ru.wert.tubus.winform.enums.EPDFViewer;
import ru.wert.tubus.chogori.pdf.PDFReader;
//import ru.wert.datapik.chogori.pdf.readers.temp.PdfIcepdfReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSNewReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSOldReader;

/**
 * Класс создает две панели: просмотрщика PDF - pdfStackPane и просмотрщика image - imageStackPane.
 * Панели меняются местами в зависимости от типа полученного для отображения файла (PDF или image)
 */
public class DraftPreviewer {

    private StackPane pdfStackPane;
    private StackPane imageStackPane;

    private ImageView imageView;
    private PDFReader pdfReader;

    private final EPDFViewer viewer;
    private final Scene scene;

    /**
     * Создает две панели просмотрщика PDF - pdfStackPane и просмотрщика image - imageStackPane.
     * @param viewer enum PDFViewer
     * @param scene Scene - сцена окна, в котором разместится IcePDF
     */
    public DraftPreviewer(EPDFViewer viewer, Scene scene) {
        this.viewer = viewer;
        this.scene = scene;
    }

    /**
     * Создает панель для просмотра image
     * Просмотр происходит с помощью imageView
     */
    private void createImageStackPane() {
        imageStackPane.getChildren().add(imageView);
        // Управление просмотрщиком

    }

    /**
     * Отображает полученный чертеж draft. Файл может быть либо pdf либо изображением image.
     * В зависимости от типа передаваемого файла, происходит переключение панели отображения,
     * которая в свою очередь передается на панель appPreviewer
     * @param draft FileFwdSlash отображаемый файл
     * @param appPreviewer StackPane панель предпросмотра
     */
    public void showDraft(FileFwdSlash draft, StackPane appPreviewer){

        String ext = FilenameUtils.getExtension(draft.getName()).toLowerCase();
        appPreviewer.getChildren().clear();
        if(ext.equals("pdf")){
            switch(viewer){
                case NEW_PDF_JS: pdfReader = new PdfJSNewReader(appPreviewer); break; //Просмотр новым pdf.js
                case OLD_PDF_JS: pdfReader = new PdfJSOldReader(appPreviewer); break; //Просмотр старым pdf.js
//                default : pdfReader = new PdfIcepdfReader(appPreviewer, scene); //Просмотр IcePDF
            }
            Platform.runLater(()->{
                pdfReader.showPDF(draft);
            });

        } else {
            Image imageDraft = new Image(DraftPreviewer.class.getResourceAsStream(String.valueOf(draft)));
            imageView.setImage(imageDraft);
            imageView.setFitWidth(imageDraft.getWidth());
            imageView.setFitHeight(imageDraft.getHeight());
            appPreviewer.getChildren().add(imageView);
        }

    }

}
