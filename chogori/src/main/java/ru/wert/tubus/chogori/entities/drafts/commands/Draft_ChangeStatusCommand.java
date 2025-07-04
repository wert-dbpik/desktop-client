package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_ChangeStatusController;
import ru.wert.tubus.chogori.entities.drafts.Draft_RenameController;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Draft_ChangeStatusCommand implements ICommand {

    private final Draft_TableView tableView;
    private final Draft selectedDraft;

    /**
     * Конструктор вызывается из контекстного меню таблицы
     * @param tableView Draft_TableView
     */
    public Draft_ChangeStatusCommand(Draft_TableView tableView) {
        this.tableView = tableView;
        this.selectedDraft = tableView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void execute() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/changeStatus.fxml"));
            Parent parent = loader.load();
            Draft_ChangeStatusController controller = loader.getController();

            String title = "";
            if(selectedDraft != null) {
                controller.init(tableView, selectedDraft);
                title = selectedDraft.getPassport().toUsefulString();
            }

            new WindowDecoration("Статус чертежа " + title,
                    parent, false, WF_MAIN_STAGE, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
