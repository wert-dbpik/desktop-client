package ru.wert.datapik.chogori.popups;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ru.wert.datapik.chogori.statics.AppStatic;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

//import java.awt.*;

public class PastePopup extends PopupControl {
    private final TextField textField;

    public PastePopup(TextField textField) {
        this.textField = textField;
    }

    public void showHint(){
        String clipboardData = "";
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboardData = (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        String text = "Вставить:\n" + clipboardData;
        Button btnPaste = new Button(text);
        btnPaste.setStyle("-fx-text-alignment: center; -fx-font-style: italic;");

        String finalClipboardData = clipboardData;
        btnPaste.setOnAction((e -> {
            textField.setText(finalClipboardData);
            AppStatic.closeWindow(e);

        }));
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: transparent; -fx-border-style: solid;");
        pane.getChildren().add(btnPaste);
        getScene().setRoot(pane);
        setAutoHide(true);

        Point2D p = textField.localToScene(0.0, 0.0);
        double indentByX = 10.0;
        double indentByY = 2.0;

        show(textField.getScene().getWindow(),
                p.getX() + indentByX + textField.getScene().getX() + textField.getScene().getWindow().getX(),
                p.getY() + indentByY + textField.getHeight() + textField.getScene().getY() + textField.getScene().getWindow().getY());

    }
}
