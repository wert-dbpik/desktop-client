package ru.wert.datapik.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.serviceQUICK.DraftQuickService;
import ru.wert.datapik.client.entity.serviceQUICK.PassportQuickService;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PASSPORTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ATTENTION;

public class Draft_RenameController {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnRename;

    @FXML
    private TextField tfName;

    @FXML
    private StackPane spIndicator;

    private Draft_TableView tableView;
    private Passport passport;
    private Draft selectedDraft = null;

    /**
     *
     * @param tableView Draft_TableView
     * @param object object, либо Draft либо Passport
     */
    public void init(Draft_TableView tableView, Object object){
        this.tableView = tableView;
        if(object instanceof Draft){
            this.selectedDraft = (Draft)object;
            passport = this.selectedDraft.getPassport();
        }
        else if(object instanceof Passport){
            this.passport = (Passport)object;
        }

        String oldName = passport.getName();
        tfName.setText(oldName);
    }

    @FXML
    void cancel(ActionEvent event) {
        AppStatic.closeWindow(event);
    }

    @FXML
    void rename(ActionEvent event) {
        spIndicator.setVisible(false);
        btnRename.setDisable(true);

        String newName = tfName.getText().trim();
        passport.setName(newName);

        Task<Void> renameTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                CH_PASSPORTS.update(passport);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                //Глубокое обновление - перезагружаем
                PassportQuickService.getInstance();
                DraftQuickService.getInstance();
                //А теперь обновляем
                Platform.runLater(()->{
                    tableView.updateView();
                    if(selectedDraft != null) {
                        tableView.scrollTo(selectedDraft);
                        tableView.getSelectionModel().select(selectedDraft);
                    }
                });
                AppStatic.closeWindow(event);
            }

            @Override
            protected void failed() {
                super.failed();
                spIndicator.setVisible(false);
                btnRename.setDisable(false);
                Platform.runLater(()->{
                    Warning1.create($ATTENTION, "Не удалось переименовать чертеж", "Возможно, сервер не доступен");
                    AppStatic.closeWindow(event);
                });

            }

            @Override
            protected void cancelled() {
                super.cancelled();
                spIndicator.setVisible(false);
                btnRename.setDisable(false);
            }
        };

        new Thread(renameTask).start();

    }

    @FXML
    void initialize() {
        AppStatic.createSpIndicator(spIndicator);
    }

}
