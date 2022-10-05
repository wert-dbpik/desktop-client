package ru.wert.datapik.chogori.pdf.readers;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;
import ru.wert.datapik.chogori.pdf.PDFReader;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class PdfJSNewReader implements PDFReader {

    WebView webview;
    WebEngine engine;

    public PdfJSNewReader(StackPane stackPaneForPDF) {

        webview = new WebView();
        engine = webview.getEngine();

        stackPaneForPDF.getChildren().add(webview);

        String url = getClass().getResource("/chogori-pdfjs/pdfjs-2.6.347/viewer.html").toExternalForm();
        engine.setUserStyleSheetLocation(getClass().getResource("/chogori-pdfjs/pdfjs-2.6.347/viewer.css").toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.load(url);
    }

    @Override
    public void showPDF(File file) {

        try {
            byte[] data = FileUtils.readFileToByteArray(file);
            //Base64 from java.util
            String base64 = Base64.getEncoder().encodeToString(data);
            //This must be ran on FXApplicationThread
            webview.getEngine().executeScript("openFileFromBase64('" + base64 + "')");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
