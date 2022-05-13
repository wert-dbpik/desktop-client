package ru.wert.datapik.utils.logging;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class LoggingController implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apChangeHistoryPatch;

    @FXML
    private VBox vbChangeHistory;

    @FXML
    private HBox changeHistoryButtons;

    private AppLog_TableView tableView;

    @FXML
    void initialize(){
        tableView = new AppLog_TableView("Логи", false, false);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbChangeHistory.getChildren().add(tableView);
    }

    @Override //UpdatableTabController
    public void updateTab() {
        tableView.updateTableView();
    }

    @Override //SearchableTab
    public void tuneSearching() {
        Platform.runLater(()->tableView.requestFocus());
    }
}
