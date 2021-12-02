package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.entities.drafts.Draft_ACCController;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.drafts.RenameDraftController;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

public class Draft_RenameCommand implements ICommand {

    private Draft_TableView tableView;

    /**
     *
     * @param tableView Draft_TableView
     */
    public Draft_RenameCommand(Draft_TableView tableView) {
        this.tableView = tableView;

    }
    @Override
    public void execute() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/drafts/renameDraft.fxml"));
            Parent parent = loader.load();
            RenameDraftController controller = loader.getController();


            Draft selectedDraft = tableView.getSelectionModel().getSelectedItem();
            controller.init(tableView, selectedDraft);

            new WindowDecoration("Переимнование " + selectedDraft.getPassport().getName(),
                    parent, false, CH_MAIN_STAGE, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
