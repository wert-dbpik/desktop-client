package ru.wert.tubus.chogori.common.utils;


import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.InputStream;

public class ClipboardUtils {

    //To copy

    public static ClipboardContent copyToClipboardText(String s) {

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(s);
        clipboard.setContent(content);

        return content;

    }

    public static String getStringFromClipboard(){
        final String[] string = new String[1];
        Platform.runLater(()->{
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            string[0] = clipboard.getString();
        });

//        final ClipboardContent content = new ClipboardContent();

        return string[0];
    }

    public static String getStringFromClipboardOutOfFXThread(){
        final String[] string = new String[1];
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            string[0] = clipboard.getString();

        return string[0];
    }

    public static void clear(){
        Platform.runLater(()->{
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.clear();
        });
    }

    public static void copyToClipboardImage(Label lbl) {

        WritableImage snapshot = lbl.snapshot(new SnapshotParameters(), null);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.putImage(snapshot);
        clipboard.setContent(content);

    }

    public static void copyToClipboardImageFromFile(String path) {

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        content.putImage(ClipboardUtils.getImage(path));
        clipboard.setContent(content);

    }

    public static Image getImage(String path) {

        InputStream is = ClipboardUtils.class.getResourceAsStream(path);
        return new Image(is);
    }


    public static ImageView setIcon(String path) {

        InputStream is = ClipboardUtils.class.getResourceAsStream(path);
        ImageView iv = new ImageView(new Image(is));

        iv.setFitWidth(100);
        iv.setFitHeight(100);
        return iv;
    }
}
