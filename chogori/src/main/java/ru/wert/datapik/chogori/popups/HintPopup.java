package ru.wert.datapik.chogori.popups;

import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class HintPopup extends PopupControl {

    private final Control node;
    private final String text;
    private final double indentByX;

    public HintPopup(Control node, String text, double indentByX) {
        this.node = node;
        this.text = text;
        this.indentByX = indentByX;

    }

    public void showHint(){

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: black; -fx-font-style: oblique");
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: white; -fx-border-style: solid; -fx-padding: 5 5 5 5");
        pane.getChildren().add(label);
        getScene().setRoot(pane);
        setAutoHide(true);

        Point2D p = node.localToScene(0.0, 0.0);
//        double indentByX = 0.0;
        double indentByY = 2.0;

        show(node.getScene().getWindow(),
                p.getX() + indentByX + node.getScene().getX() + node.getScene().getWindow().getX(),
                p.getY() + indentByY + node.getHeight() + node.getScene().getY() + node.getScene().getWindow().getY());

    }

    public void closeHint(){
        hide();
    }
}
