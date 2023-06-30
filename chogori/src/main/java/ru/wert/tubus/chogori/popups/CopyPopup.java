package ru.wert.tubus.chogori.popups;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.AnchorPane;
import ru.wert.tubus.chogori.statics.AppStatic;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CopyPopup extends PopupControl {

    public CopyPopup(){}

    public void showHint(Label label) {
        String text = "Копировать в буфер:\n" + label.getText();
        Button btnCopy = new Button(text);
        btnCopy.setStyle("-fx-text-alignment: center; -fx-font-style: italic;");
        btnCopy.setOnAction((e -> {
            StringSelection selection = new StringSelection(label.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            AppStatic.closeWindow(e);

        }));
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: transparent; -fx-border-style: solid; -fx-border-radius: 2pt");
        pane.getChildren().add(btnCopy);
        getScene().setRoot(pane);
        setAutoHide(true);

        Point2D p = label.localToScene(0.0, 0.0);

        double indentByX = 50.0;
        show(label.getScene().getWindow(),
                p.getX() + indentByX + label.getScene().getX() + label.getScene().getWindow().getX(),
                p.getY() + label.getHeight() + label.getScene().getY() + label.getScene().getWindow().getY());
    }

}
