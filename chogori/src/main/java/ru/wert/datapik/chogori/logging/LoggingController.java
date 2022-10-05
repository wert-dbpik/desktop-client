package ru.wert.datapik.chogori.logging;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

import static ru.wert.datapik.chogori.images.BtnImages.BTN_UPDATE_IMG;

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

        Button btnUpdate = new Button();
        btnUpdate.setId("patchButton");
        btnUpdate.setGraphic(new ImageView(BTN_UPDATE_IMG));
        btnUpdate.setTooltip(new Tooltip("Обновить логи"));
        btnUpdate.setOnAction(event -> {
            tableView.updateTableView();
        });
        changeHistoryButtons.getChildren().add(btnUpdate);
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
