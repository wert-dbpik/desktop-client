package ru.wert.datapik.utils.logging;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

public class ChangeHistoryController implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apChangeHistoryPatch;

    @FXML
    private VBox vbChangeHistory;

    @FXML
    private HBox changeHistoryButtons;

    private AppLog_TableView tableView;

    @FXML
    void initialize(){
        tableView = new AppLog_TableView("Логи", true, false);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbChangeHistory.getChildren().add(tableView);
    }

    @Override
    public void updateTab() {
        tableView.updateTableView();
    }

    @Override
    public void tuneSearching() {
        Platform.runLater(()->tableView.requestFocus());
    }
}
