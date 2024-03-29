package ru.wert.tubus.chogori.logging;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.interfaces.SearchableTab;
import ru.wert.tubus.client.interfaces.UpdatableTabController;
import ru.wert.tubus.chogori.images.BtnImages;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

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

        Button btnUpdate = new Button();
        btnUpdate.setId("patchButton");
        btnUpdate.setGraphic(new ImageView(BtnImages.BTN_UPDATE_IMG));
        btnUpdate.setTooltip(new Tooltip("Обновить историю изменений"));
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
        CH_SEARCH_FIELD.changeSearchedTableView(tableView, "ЛОГ");
    }
}
