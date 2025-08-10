package ru.wert.tubus.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.serviceQUICK.DraftQuickService;
import ru.wert.tubus.client.entity.serviceQUICK.PassportQuickService;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.$ATTENTION;

public class Draft_RenameController {

    @FXML private Button btnCancel;
    @FXML private Button btnRename;
    @FXML private TextField tfName;
    @FXML private StackPane spIndicator;

    private Draft_TableView tableView;
    private Passport passport;
    private Draft selectedDraft = null;
    private volatile boolean operationCancelled = false;

    public void init(Draft_TableView tableView, Object object) {
        this.tableView = tableView;
        if (object instanceof Draft) {
            this.selectedDraft = (Draft) object;
            passport = this.selectedDraft.getPassport();
        } else if (object instanceof Passport) {
            this.passport = (Passport) object;
        }
        tfName.setText(passport.getName());
    }

    @FXML
    void cancel(ActionEvent event) {
        operationCancelled = true;
        AppStatic.closeWindow(event);
    }

    @FXML
    void rename(ActionEvent event) {
        prepareUIForOperation();

        String newName = tfName.getText().trim();
        if (newName.isEmpty()) {
            handleEmptyName();
            return;
        }

        passport.setName(newName);

        Task<Void> renameTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (operationCancelled) return null;

                try {
                    ChogoriServices.CH_QUICK_PASSPORTS.update(passport);
                    return null;
                } catch (Exception e) {
                    if (!operationCancelled) {
                        throw e;
                    }
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (!operationCancelled) {
                        updateServicesAndUI();
                        AppStatic.closeWindow(event);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    if (!operationCancelled) {
                        resetUI();
                        Warning1.create($ATTENTION, "Ошибка переименования", "Сервер недоступен");
                    }
                });
            }

            @Override
            protected void cancelled() {
                Platform.runLater(()->resetUI());
            }
        };

        btnCancel.setOnAction(cancelEvent -> {
            operationCancelled = true;
            renameTask.cancel(false); // Не прерываем поток
            AppStatic.closeWindow(cancelEvent);
        });

        new Thread(renameTask).start();
    }

    private void prepareUIForOperation() {
        operationCancelled = false;
        spIndicator.setVisible(true);
        btnRename.setDisable(true);
        btnCancel.setDisable(false);
    }

    private void updateServicesAndUI() {
        PassportQuickService.getInstance();
        DraftQuickService.getInstance();
        tableView.updateTableView();
    }

    private void handleEmptyName() {
        Platform.runLater(() -> {
            resetUI();
            Warning1.create($ATTENTION, "Не указано имя", "Введите новое имя чертежа");
        });
    }

    private void resetUI() {
        spIndicator.setVisible(false);
        btnRename.setDisable(false);
        btnCancel.setDisable(false);
    }

    @FXML
    void initialize() {
        AppStatic.createSpIndicator(spIndicator);
        btnCancel.setOnAction(this::cancel);
    }
}
