package ru.wert.tubus.chogori.crashReport;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

public class CrashReportsController implements UpdatableTabController {

    @FXML
    private AnchorPane apChangeHistoryPatch;

    @FXML
    private VBox vbChangeHistory;

    @FXML
    private HBox changeHistoryButtons;

    private CrashReport_TableView tableView;

    @FXML
    void initialize(){
        tableView = new CrashReport_TableView("Отчеты о крашах", true, true);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbChangeHistory.getChildren().add(tableView);

        Button btnUpdate = new Button();
        btnUpdate.setId("patchButton");
        btnUpdate.setGraphic(new ImageView(BtnImages.BTN_UPDATE_IMG));
        btnUpdate.setTooltip(new Tooltip("Обновить список"));
        btnUpdate.setOnAction(event -> {
            tableView.updateTableView();
        });
        changeHistoryButtons.getChildren().add(btnUpdate);
    }

    @Override //UpdatableTabController
    public void updateTab() {
        tableView.updateTableView();
    }

}
