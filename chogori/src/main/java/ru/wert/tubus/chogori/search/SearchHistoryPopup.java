package ru.wert.tubus.chogori.search;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchHistoryPopup extends PopupControl {
    static final int MAX_ROWS = 10;
    static final double ROW_HEIGHT = 26;

    public void showHistory() {

        SearchHistoryListView historyList = SearchHistoryListView.getInstance();
        historyList.setPrefWidth(300);
        int rows = Math.min(historyList.getItems().size(), MAX_ROWS);
        rows = rows == 0 ? 1 : rows;
        historyList.setPrefHeight(rows * ROW_HEIGHT);

        historyList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.hide();
        });

        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().add(historyList);
        getScene().setRoot(pane);
        setAutoHide(true);

        Point2D p = CH_SEARCH_FIELD.localToScene(0.0, 0.0);

        double indentByX = 0.0;
        double indentByY = 5.0;
        show(CH_SEARCH_FIELD.getScene().getWindow(),
                p.getX() + indentByX + CH_SEARCH_FIELD.getScene().getX() + CH_SEARCH_FIELD.getScene().getWindow().getX(),
                p.getY() + indentByY + CH_SEARCH_FIELD.getHeight() + CH_SEARCH_FIELD.getScene().getY() + CH_SEARCH_FIELD.getScene().getWindow().getY());
    }

}
