package ru.wert.tubus.chogori.search;

import javafx.geometry.Point2D;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.AnchorPane;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchHistoryPopup extends PopupControl {
    static final double n = 10.0; //Высота строки

    public void showHistory() {

        SearchHistoryListView historyList = SearchHistoryListView.getInstance();
        historyList.setPrefWidth(300);
        historyList.setMinHeight(n);
        historyList.setMaxHeight(10 * n);

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
