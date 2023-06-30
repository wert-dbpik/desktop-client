package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.application.Platform;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.common.contextMenuACC.FormViewACCWindow;
import ru.wert.tubus.chogori.entities.drafts.Draft_ACCController;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.winform.enums.EOperation;

public class Draft_AddFolderCommand implements ICommand {

    private Draft_TableView tableView;

    /**
     *
     * @param tableView Draft_TableView
     */
    public Draft_AddFolderCommand(Draft_TableView tableView) {
        this.tableView = tableView;

    }
    @Override
    public void execute() {
        Platform.runLater(()->{
            FormViewACCWindow<Draft> accWindow = new FormViewACCWindow<>();
            accWindow.create(EOperation.ADD_FOLDER, tableView, tableView.getCommands(), tableView.getAccWindowRes());
            Draft_ACCController controller = (Draft_ACCController) accWindow.getAccController();
            tableView.setAccController(controller);
        });
    }
}
