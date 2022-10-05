package ru.wert.datapik.chogori.entities.drafts.commands;

import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.chogori.entities.drafts.Draft_ACCController;
import ru.wert.datapik.chogori.entities.drafts.Draft_TableView;
import ru.wert.datapik.winform.enums.EOperation;

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

        FormViewACCWindow<Draft> accWindow = new FormViewACCWindow<>();
        accWindow.create(EOperation.ADD_FOLDER, tableView, tableView.getCommands(), tableView.getAccWindowRes());
        Draft_ACCController controller = (Draft_ACCController) accWindow.getAccController();
        tableView.setAccController(controller);

    }
}
