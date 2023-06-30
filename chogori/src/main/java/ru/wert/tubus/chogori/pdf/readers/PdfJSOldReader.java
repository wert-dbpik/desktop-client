package ru.wert.tubus.chogori.pdf.readers;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;
import ru.wert.tubus.chogori.pdf.PDFReader;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class PdfJSOldReader implements PDFReader {

    WebView webview;

    public PdfJSOldReader(StackPane stackPaneForPDF) {

        webview = new WebView();
        WebEngine engine = webview.getEngine();

        stackPaneForPDF.getChildren().add(webview);

        String url = getClass().getResource("/chogori-pdfjs/pdfjs-2.3.200/viewer.html").toExternalForm();
        //We add our stylesheet.
        engine.setUserStyleSheetLocation(getClass().getResource("/chogori-pdfjs/pdfjs-2.3.200/viewer.css").toExternalForm());
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
            Platform.runLater(()->{
                webview.getEngine().executeScript("openFileFromBase64('" + base64 + "')");
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
