package ru.wert.tubus.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.serviceQUICK.DraftQuickService;
import ru.wert.tubus.client.entity.serviceQUICK.PassportQuickService;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.$ATTENTION;

public class Draft_ChangeStatusController {

    @FXML private Button btnCancel;
    @FXML private Button btnSaveStatus;
    @FXML private ComboBox<EDraftStatus> cbStatus;
    @FXML private StackPane spIndicator;

    private Draft_TableView tableView;
    private Draft selectedDraft = null;
    private volatile Task<Void> statusChangeTask;

    public void init(Draft_TableView tableView, Object object) {
        this.tableView = tableView;
        if (object instanceof Draft) {
            this.selectedDraft = (Draft) object;
            EDraftStatus status = EDraftStatus.getStatusById(selectedDraft.getStatus());

            ObservableList<EDraftStatus> statuses = FXCollections.observableArrayList(EDraftStatus.values());
            statuses.remove(EDraftStatus.UNKNOWN);
            cbStatus.setItems(statuses);
            cbStatus.getSelectionModel().select(status);
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        if (statusChangeTask != null && !statusChangeTask.isDone()) {
            statusChangeTask.cancel(true);
        }
        AppStatic.closeWindow(event);
    }

    @FXML
    void saveStatus(ActionEvent event) {
        spIndicator.setVisible(true);
        btnSaveStatus.setDisable(true);
        btnCancel.setDisable(false);

        selectedDraft.setStatus(cbStatus.getSelectionModel().getSelectedIndex());

        statusChangeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (isCancelled()) return null;
                ChogoriServices.CH_QUICK_DRAFTS.update(selectedDraft);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    PassportQuickService.getInstance();
                    DraftQuickService.getInstance();
                    tableView.updateTableView();
                    AppStatic.closeWindow(event);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    resetUI();
                    Warning1.create($ATTENTION, "Не удалось изменить статус чертежа", "Возможно, сервер не доступен");
                });
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                Platform.runLater(() -> resetUI());
            }
        };

        Thread taskThread = new Thread(statusChangeTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }

    // Метод для восстановления исходного состояния UI
    private void resetUI() {
        spIndicator.setVisible(false);
        btnSaveStatus.setDisable(false);
        btnCancel.setDisable(false);
    }

    @FXML
    void initialize() {
        AppStatic.createSpIndicator(spIndicator);
        // Явная установка обработчика для кнопки отмены
        btnCancel.setOnAction(this::cancel);

        cbStatus.setStyle("-fx-font-size: 16px;");

        // Устанавливаем cellFactory для отображения statusName в выпадающем списке
        cbStatus.setCellFactory(param -> new ListCell<EDraftStatus>() {
            @Override
            protected void updateItem(EDraftStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getStatusName());
                }
            }
        });

        // Устанавливаем buttonCell для отображения выбранного значения
        cbStatus.setButtonCell(new ListCell<EDraftStatus>() {
            @Override
            protected void updateItem(EDraftStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getStatusName());
                }
            }
        });
    }
}
